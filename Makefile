KB_RUNTIME ?= /kb/runtime
KB_TOP ?= /kb/dev_container
ANT = ant
SETUP_ANT = $(ANT) -DDEPLOY_RUNTIME=$(KB_RUNTIME) -DTARGET=$(KB_TOP)

default: compile

deploy: distrib

deploy-all: distrib

test: test-client-server
	@echo "finished all tests"

test-client-server:
	$(SETUP_ANT) test
	cd test; bash ./test_gen_java_types.sh

compile:
	$(SETUP_ANT)

distrib:
	$(SETUP_ANT) dist

clean:
	$(SETUP_ANT) clean
