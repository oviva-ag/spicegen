
.PHONY: build fmt

build:
	goreleaser release --snapshot --clean
	cp ./dist/spicegen_darwin_arm64_v8.0/spicegen_darwin_arm64_v8.0 ../model/src/main/resources/spicegen_darwin_arm64
	cp ./dist/spicegen_linux_amd64_v1/spicegen_linux_amd64_v1 ../model/src/main/resources/spicegen_linux_amd64_v1
	cp ./dist/spicegen_darwin_amd64_v1/spicegen_darwin_amd64_v1 ../model/src/main/resources/spicegen_darwin_amd64_v1
	cp ./dist/spicegen_linux_arm64_v8.0/spicegen_linux_arm64_v8.0 ../model/src/main/resources/spicegen_linux_arm64
	cp ./dist/spicegen_linux_386_sse2/spicegen_linux_386_sse2 ../model/src/main/resources/spicegen_linux_386

fmt:
	gofumpt -w .
