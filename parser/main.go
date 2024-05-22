package main

import (
	"container/list"
	"encoding/json"
	"flag"
	"fmt"
	"io"
	"log"
	"os"

	"github.com/authzed/spicedb/pkg/schemadsl/dslshape"
	"github.com/authzed/spicedb/pkg/schemadsl/input"
	"github.com/authzed/spicedb/pkg/schemadsl/parser"
)

func main() {
	var schemaPath string
	flag.StringVar(&schemaPath, "schema-path", "", "the path to the SpiceDB schema")

	var outPath string
	flag.StringVar(&outPath, "out-path", "", "the path to output the AST to")

	flag.Parse()

	if schemaPath == "" {
		log.Fatal("no schema path defined!")
		flag.Usage()
	}

	if len(outPath) == 0 {
		outPath = "-"
	}

	log.Printf("generating AST for schema %q to %q", schemaPath, outPath)

	out := os.Stdout
	if outPath != "-" {
		f, err := os.OpenFile(outPath, os.O_RDWR|os.O_CREATE|os.O_TRUNC, 0644)
		if err != nil {
			log.Fatal(err)
		}
		defer f.Close()
		out = f
	}

	f, err := os.Open(schemaPath)
	if err != nil {
		log.Fatal(err)
	}

	r, err := Parse(schemaPath, f)
	if err != nil {
		log.Fatal(err)
	}

	enc := json.NewEncoder(out)
	enc.SetIndent("", " ")
	err = enc.Encode(r)
	if err != nil {
		log.Fatal(err)
	}
}

func Parse(sourceName string, r io.Reader) (Node, error) {
	b, err := io.ReadAll(r)
	if err != nil {
		return nil, fmt.Errorf("failed to read schema %s: %w", sourceName, err)
	}

	rawRoot := parser.Parse(createAstNode, input.Source(sourceName), string(b))
	root := rawRoot.(*astNode)

	mapped := parse(root)
	return mapped, nil
}

func createAstNode(_ input.Source, kind dslshape.NodeType) parser.AstNode {
	return &astNode{
		nodeType:   kind,
		properties: make(map[string]interface{}),
		children:   make(map[string]*list.List),
	}
}
