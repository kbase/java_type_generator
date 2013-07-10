KB_ROOT ?= /kb
DEPLOY_RUNTIME ?= $(KB_ROOT)/runtime
TARGET ?= $(KB_ROOT)/deployment
ANT = ant
SETUP_ANT = $(ANT) -DDEPLOY_RUNTIME=$(DEPLOY_RUNTIME) -DTARGET=$(TARGET)

default: all

test: test-client-server
	@echo "finished all tests"

test-client-server:
	$(SETUP_ANT) test

deploy: all

deploy-all: all

all:
	$(SETUP_ANT) dist

clean:
	$(SETUP_ANT) clean
