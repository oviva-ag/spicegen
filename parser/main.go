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
	"github.com/davecgh/go-spew/spew"
)

func main() {
	var schemaPath string
	flag.StringVar(&schemaPath, "schema-path", "", "the path to the SpiceDB schema")
	flag.Parse()

	if schemaPath == "" {
		log.Fatal("no schema path defined!")
		flag.Usage()
	}

	f, err := os.Open(schemaPath)
	if err != nil {
		log.Fatal(err)
	}

	r, err := Parse(schemaPath, f)
	if err != nil {
		log.Fatal(err)
	}

	enc := json.NewEncoder(os.Stdout)
	enc.SetIndent("", " ")
	err = enc.Encode(r)
	if err != nil {
		log.Fatal(err)
	}
}

func Parse(sourceName string, r io.Reader) (Node, error) {
	b, err := io.ReadAll(r)
	if err != nil {
		return nil, fmt.Errorf("failed to read schema %s", sourceName)
	}

	rawRoot := parser.Parse(createAstNode, input.Source(sourceName), string(b))
	root := rawRoot.(*astNode)

	spew.Dump(root)

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
