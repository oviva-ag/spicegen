package main

import (
	"container/list"
	"fmt"
	"github.com/authzed/spicedb/pkg/schemadsl/dslshape"
	"github.com/authzed/spicedb/pkg/schemadsl/parser"
	"sort"
	"strings"
)

type astNode struct {
	nodeType   dslshape.NodeType
	properties map[string]any // int or string
	children   map[string]*list.List
}

func (a *astNode) GetType() dslshape.NodeType {
	return a.nodeType
}

func (a *astNode) Connect(predicate string, other parser.AstNode) {
	if a.children[predicate] == nil {
		a.children[predicate] = list.New()
	}

	a.children[predicate].PushBack(other.(*astNode))
}

func (a *astNode) MustDecorate(property string, value string) parser.AstNode {
	if _, ok := a.properties[property]; ok {
		panic(fmt.Sprintf("Existing key for property %s\n\tNode: %v", property, a.properties))
	}

	a.properties[property] = value
	return a
}

func (a *astNode) MustDecorateWithInt(property string, value int) parser.AstNode {
	if _, ok := a.properties[property]; ok {
		panic(fmt.Sprintf("Existing key for property %s\n\tNode: %v", property, a.properties))
	}

	a.properties[property] = value
	return a
}

const indentation = 2

func (a *astNode) String() string {
	return a.StringWithIndentation(indentation)
}

func (a *astNode) StringWithIndentation(indentation int) string {
	parseTree := ""
	parseTree = parseTree + strings.Repeat(" ", indentation)
	parseTree = parseTree + fmt.Sprintf("%v", a.nodeType)
	parseTree = parseTree + "\n"

	keys := make([]string, 0)

	for key := range a.properties {
		keys = append(keys, key)
	}

	sort.Strings(keys)

	for _, key := range keys {
		parseTree = parseTree + strings.Repeat(" ", indentation+2)
		parseTree = parseTree + fmt.Sprintf("%s = %v", key, a.properties[key])
		parseTree = parseTree + "\n"
	}

	keys = make([]string, 0)

	for key := range a.children {
		keys = append(keys, key)
	}

	sort.Strings(keys)

	for _, key := range keys {
		value := a.children[key]
		parseTree = parseTree + fmt.Sprintf("%s%v =>", strings.Repeat(" ", indentation+2), key)
		parseTree = parseTree + "\n"

		for e := value.Front(); e != nil; e = e.Next() {
			child := e.Value.(*astNode)
			parseTree = parseTree + child.StringWithIndentation(indentation+4)
		}
	}

	return parseTree
}
