KB_TOP ?= /kb/dev_container
KB_RUNTIME ?= /kb/runtime
DEPLOY_RUNTIME ?= $(KB_RUNTIME)
TARGET ?= /kb/deployment
ANT = ant

default: compile

deploy: distrib

deploy-all: distrib

test: test-client-server
	@echo "finished all tests"

test-client-server:
	$(ANT) -DDEPLOY_RUNTIME=$(DEPLOY_RUNTIME) -DTARGET=$(KB_TOP) test

compile:
	$(ANT) -DDEPLOY_RUNTIME=$(DEPLOY_RUNTIME) -DTARGET=$(KB_TOP)

distrib:
	$(ANT) -DDEPLOY_RUNTIME=$(DEPLOY_RUNTIME) -DTARGET=$(TARGET) dist

clean:
	$(ANT) clean
