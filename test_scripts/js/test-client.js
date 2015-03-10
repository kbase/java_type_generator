//
// usage:
//   casperjs test test-client.js --fail-fast --tests=[test_cfg_file] --endpoint=[server_url] --token=[token]
//
//


casper.test.begin('JS Client Tests', function suite(test) {

    // First test that we have all the arguments we need
    test.assertTruthy(casper.cli.get('tests'),
        'Argument "tests" must be defined and set to the test config file.');
    test.assertTruthy(casper.cli.get('endpoint'),
		'Argument "endpoint" must be defined and set to service url.');

    //test.assertTruthy(casper.cli.get('token'),
    //	'Argument "token" must be defined and set to a valid auth token.');

    // Read the config and make sure we have the right things
    var fs = require('fs');
    var utils = require('utils');
    var testcfg = JSON.parse(fs.read(casper.cli.get('tests')));

    test.assert(testcfg.hasOwnProperty('package'),
		'Test config json must define field "package" (used to find the JS file name)');
	test.assert(testcfg.hasOwnProperty('class'),
		'Test config json must define field "class" (used to find the JS class)');
    test.assert(testcfg.hasOwnProperty('tests'),
        'Test config json must define field "tests" (used to find the actual test configs)');

    // load the JQuery and the JS file
    phantom.injectJs('jquery-1.10.2.min.js');
    phantom.injectJs(testcfg.package+".js")

    // instantiate the clients
    var noauthClient = new window[testcfg['class']](casper.cli.get('endpoint'));
    test.assertTruthy(noauthClient,'Unauthenticated client instantiated.');
    var authClient   = new window[testcfg['class']](casper.cli.get('endpoint'),{});
    test.assertTruthy(noauthClient,'Authenticated client instantiated.');

    // run through each test
    var tests = testcfg.tests;
    for(var t=0; t<tests.length; t++) {
        var client = noauthClient;
        if(tests[t].auth) {
            client=authClient;
        }

        test.assert(client.hasOwnProperty(tests[t].method),'--------- '+tests[t].method+' is defined ----------');
        test.assert(typeof client[tests[t].method] === 'function',tests[t].method+' is actually a function');
        //console.log(noauthClient[tests[t].method]);
        
        var promise = client[tests[t].method](tests[t].params);
        console.log(promise);
        promise.done(function(result) {
            console.log('here');
        })
        /*casper.test.begin('test method', function suite(test){

            client[tests[t].method](tests[t].params,
                function(result) {
                    console.log(result);
                    test.done();
                },
                function(err) {
                    console.log('got error:',err);
                    test.done();
                });
        });*/

        //console.log(JSON.stringify(tests[t],null, 2));
    }

	/*casper.test.begin('test method', function suite(test){

		casper.start(casper.cli.get("tests"), function() {console.log('got_something')}); 


		test.assertEquals(1, 1);
	    test.done();


	});*/

    //test.done();

});
	




//casper.test.done();