package main

import (
	"encoding/json"
	"fmt"
	"os"
	"testing"
)

func TestParseBasic(t *testing.T) {

	f, err := os.Open("./basic.zed")
	if err != nil {
		t.Fatal(err)
	}
	defer f.Close()

	root, _ := Parse("basic", f)

	j, _ := json.MarshalIndent(root, "", " ")
	fmt.Println(string(j))
}

func TestFixtures(t *testing.T) {

	cases := []string{
		"files", "groups", "platform", "recursive", "synthetic", "oviva",
	}

	for _, tc := range cases {
		t.Run(tc, func(t *testing.T) {

			f, err := os.Open(fmt.Sprintf("./fixtures/%s.zed", tc))
			if err != nil {
				t.Fatal(err)
			}
			defer f.Close()

			root, _ := Parse(tc, f)

			j, _ := json.MarshalIndent(root, "", " ")

			err = os.WriteFile(fmt.Sprintf("./fixtures/%s_ast.json", tc), j, 0644)
			if err != nil {
				t.Fatal(err)
			}
			fmt.Println(string(j))
		})
	}
}
