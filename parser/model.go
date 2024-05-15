package main

import (
	"github.com/authzed/spicedb/pkg/schemadsl/dslshape"
)

// go install github.com/campoy/jsonenums

//go:generate jsonenums -type=NodeType
type NodeType int

const (

	// Top-level
	NodeTypeError   NodeType = NodeType(NodeType(dslshape.NodeTypeError))
	NodeTypeFile    NodeType = NodeType(dslshape.NodeTypeFile)
	NodeTypeComment NodeType = NodeType(dslshape.NodeTypeComment)

	NodeTypeDefinition       NodeType = NodeType(dslshape.NodeTypeDefinition)
	NodeTypeCaveatDefinition NodeType = NodeType(dslshape.NodeTypeCaveatDefinition)

	NodeTypeCaveatParameter  NodeType = NodeType(dslshape.NodeTypeCaveatParameter)
	NodeTypeCaveatExpression NodeType = NodeType(dslshape.NodeTypeCaveatExpression)

	NodeTypeRelation   NodeType = NodeType(dslshape.NodeTypeRelation)
	NodeTypePermission NodeType = NodeType(dslshape.NodeTypePermission)

	NodeTypeTypeReference         NodeType = NodeType(dslshape.NodeTypeTypeReference)
	NodeTypeSpecificTypeReference NodeType = NodeType(dslshape.NodeTypeSpecificTypeReference)
	NodeTypeCaveatReference       NodeType = NodeType(dslshape.NodeTypeCaveatReference)

	NodeTypeUnionExpression     NodeType = NodeType(dslshape.NodeTypeUnionExpression)
	NodeTypeIntersectExpression NodeType = NodeType(dslshape.NodeTypeIntersectExpression)
	NodeTypeExclusionExpression NodeType = NodeType(dslshape.NodeTypeExclusionExpression)

	NodeTypeArrowExpression NodeType = NodeType(dslshape.NodeTypeArrowExpression)

	NodeTypeIdentifier    NodeType = NodeType(dslshape.NodeTypeIdentifier)
	NodeTypeNilExpression NodeType = NodeType(dslshape.NodeTypeNilExpression)

	NodeTypeCaveatTypeReference NodeType = NodeType(dslshape.NodeTypeCaveatTypeReference)
)

type Property struct {
	Name  string `json:"name"`
	Value any    `json:"value"`
}

type Child struct {
	Predicate string      `json:"predicate"`
	Nodes     []*BaseNode `json:"nodes"`
}

type Node interface {
	Kind() NodeType
}

/*
// The source of this node.
NodePredicateSource = "input-source"

// The rune position in the input string at which this node begins.
NodePredicateStartRune = "start-rune"

// The rune position in the input string at which this node ends.
NodePredicateEndRune = "end-rune"

// A direct child of this node. Implementations should handle the ordering
// automatically for this predicate.
NodePredicateChild = "child-node"
*/
type BaseNode struct {
	NodeType  NodeType `json:"type"`
	Source    string   `json:"source,omitempty"`
	StartRune int      `json:"start_rune,omitempty"`
	EndRune   int      `json:"end_rune,omitempty"`
	Children  []Node   `json:"children,omitempty"`
}

func (b *BaseNode) Kind() NodeType {
	return b.NodeType
}

type DefinitionNode struct {
	BaseNode
	Name string `json:"name"`
}

type CommentNode struct {
	BaseNode
	Comment string `json:"comment"`
}

type SpecificTypeRefNode struct {
	BaseNode
	TypeName     string  `json:"type_name"`
	RelationName *string `json:"relation_name,omitempty"`
}
type TypeRefNode struct {
	BaseNode
	TypeRefTypes []*SpecificTypeRefNode `json:"type_ref_types"`
}
type RelationNode struct {
	BaseNode
	Name         string         `json:"name"`
	AllowedTypes []*TypeRefNode `json:"allowed_types"`
}

type PermissionNode struct {
	BaseNode
	Name string `json:"name"`
}

func parse(root *astNode) Node {
	switch root.nodeType {
	case dslshape.NodeTypeDefinition:
		return parseDefinitionNode(root)
	case dslshape.NodeTypeComment:
		return parseCommentNode(root)
	case dslshape.NodeTypeRelation:
		return parseRelationNode(root)
	case dslshape.NodeTypePermission:
		return parsePermissionNode(root)
	default:
		bn := &BaseNode{}
		populateBaseNode(bn, root)
		return bn
	}
}
func parseDefinitionNode(root *astNode) Node {

	node := &DefinitionNode{
		Name: root.properties[dslshape.NodeDefinitionPredicateName].(string),
	}

	populateBaseNode(&node.BaseNode, root)

	return node
}

func parseCommentNode(root *astNode) Node {

	node := &CommentNode{
		Comment: root.properties[dslshape.NodeCommentPredicateValue].(string),
	}

	populateBaseNode(&node.BaseNode, root)

	return node
}

func parseRelationNode(root *astNode) Node {

	fromAllowedTypes := root.children[dslshape.NodeRelationPredicateAllowedTypes]
	mappedAllowedTypes := make([]*TypeRefNode, fromAllowedTypes.Len())
	i := 0
	for e := fromAllowedTypes.Front(); e != nil; e = e.Next() {
		mappedAllowedTypes[i] = parseTypeRefNode(e.Value.(*astNode))
		i++
	}

	node := &RelationNode{
		Name:         root.properties[dslshape.NodePredicateName].(string),
		AllowedTypes: mappedAllowedTypes,
	}

	populateBaseNode(&node.BaseNode, root)

	return node
}

func parseTypeRefNode(root *astNode) *TypeRefNode {

	fromTypeRefType := root.children[dslshape.NodeTypeReferencePredicateType]
	mappedChildren := make([]*SpecificTypeRefNode, fromTypeRefType.Len())
	j := 0
	for e := fromTypeRefType.Front(); e != nil; e = e.Next() {
		mappedChildren[j] = parseSpecificTypeRefTypeNode(e.Value.(*astNode))
		j++
	}
	n := &TypeRefNode{
		TypeRefTypes: mappedChildren,
	}
	populateBaseNode(&n.BaseNode, root)
	return n
}

func parseSpecificTypeRefTypeNode(root *astNode) *SpecificTypeRefNode {
	relationName := (*string)(nil)
	fromRelationName := root.properties[dslshape.NodeSpecificReferencePredicateRelation]
	if fromRelationName != nil {
		name := fromRelationName.(string)
		relationName = &name
	}
	n := &SpecificTypeRefNode{
		TypeName:     root.properties[dslshape.NodeSpecificReferencePredicateType].(string),
		RelationName: relationName,
	}
	populateBaseNode(&n.BaseNode, root)
	return n
}
func parsePermissionNode(root *astNode) Node {

	node := &PermissionNode{
		Name: root.properties[dslshape.NodePredicateName].(string),
		//TODO: add expression
	}

	populateBaseNode(&node.BaseNode, root)

	return node
}

func populateBaseNode(bn *BaseNode, root *astNode) {
	bn.NodeType = NodeType(root.nodeType)

	source := root.properties[dslshape.NodePredicateSource]
	if source != nil {
		bn.Source = source.(string)
	}

	start := root.properties[dslshape.NodePredicateStartRune]
	if start != nil {
		bn.StartRune = start.(int)
	}

	end := root.properties[dslshape.NodePredicateEndRune]
	if end != nil {
		bn.EndRune = end.(int)
	}

	fromChildren := root.children[dslshape.NodePredicateChild]
	if fromChildren != nil && fromChildren.Len() > 0 {
		mappedChildren := make([]Node, fromChildren.Len())
		j := 0
		for e := fromChildren.Front(); e != nil; e = e.Next() {
			mappedChildren[j] = parse(e.Value.(*astNode))
			j++
		}
		bn.Children = mappedChildren
	}
}
