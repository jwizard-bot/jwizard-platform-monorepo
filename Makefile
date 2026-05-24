.DEFAULT_GOAL := no-argument

.PHONY: no-argument
no-argument:
	$(error error: running 'make' without an argument is forbidden)

.PHONY: build
build:
	./gradlew build --rerun-tasks --continue

.PHONY: build-cache
build-cache:
	./gradlew build --continue

.PHONY: format-check
format-check:
	./gradlew spotlessCheck

.PHONY: format-apply
format-apply:
	./gradlew spotlessApply

include Makefile.docker
include Makefile.jw
include Makefile.tools
