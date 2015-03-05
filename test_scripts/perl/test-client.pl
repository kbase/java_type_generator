#
# Automatic test rig for KBase perl client generation.
#
#
#
#
use strict;
use warnings;

use JSON;
use Data::Dumper;
use Test::More;
use Getopt::Long;

my $DESCRIPTION =
"
USAGE
      perl test-client.pl [options]
      
DESCRIPTION
      Conducts an automatic test of a perl client against the specified endpoint using
      methods and parameters indicated in the tests config file.
      
      --tests [filename] config file for tests
      --endpoint [url]   endpoint of the server to test
      --user [username]  username for testing authenticated calls
      --password [pswd]  password for the user for testing authenticated calls
      -h, --help         display this help message, ignore all arguments
";
      
# first parse options to get the testconfig file
my $tests_filename;
my $verbose;
my $endpoint;

my $user;
my $password;

my $help;

my $opt = GetOptions (
        "tests=s" => \$tests_filename,
        "verbose|v" => \$verbose,
        "endpoint=s" => \$endpoint,
        "user=s" => \$user,
        "password=s" => \$password,
        "help|h" => \$help,
        );

if ($help) {
    print $DESCRIPTION."\n";
    exit 0;
}


# endpoint must be defined
if (!$endpoint) {
    fail("endpoint parameter must be defined");
    done_testing();
    exit 1;
}

# tests must be defined
if (!$tests_filename) {
    fail("tests parameter must be defined");
    done_testing();
    exit 1;
}

#parse the tests
open(my $fh, "<", $tests_filename);
my $tests_string='';
while (my $line = <$fh>) {
    chomp $line;
    $tests_string .= $line;
}
close($fh);
my $tests_json = JSON->new->decode($tests_string);

my $tests = $tests_json->{tests};
my $client_module = $tests_json->{module};

if (!$tests) {
    fail("tests array not defined in test config file");
    done_testing();
    exit 1;
}
if (!$client_module) {
    fail("client module not defined in test config file");
    done_testing();
    exit 1;
}

# make sure we can import the module
my $json = JSON->new->canonical;
use_ok($client_module);

#instantiate an authenticated client and a nonauthenticated client
my $nonauthenticated_client = $client_module->new($endpoint);
ok(defined($nonauthenticated_client),"instantiating nonauthenticated client");

my $authenticated_client;
if($user && $password) {
    $authenticated_client=$client_module->new($endpoint,user_id=>$user, password=>$password);
    ok(defined($authenticated_client),"instantiating authenticated client");
}

# loop over each test case and run it against the server.  We create a new client instance
# for each test
foreach my $test (@{$tests}) {
    my $client;
    if ($test->{'auth'}) {
        if ($authenticated_client) {
            $client = $authenticated_client;
        } else {
            fail("authenticated call declared, but no user and password set");
            done_testing();
            exit 1;
        }
        
    } else {
        $client = $nonauthenticated_client;
    }
    ok(defined($client),"instantiating client");
    
    my $method  = $test->{method};
    my $params  = $test->{params};
    my $outcome = $test->{outcome};
    my @result;
    {
        no strict "refs";
        ok($client->can($method), 'method "'.$method.'" exists');
        eval { @result = $client->$method(@{$params}); };
        if($@) {
            ok($outcome->{status} eq 'fail', 'expected failure, and yes it failed');
            # could do more checks here for different failure modes
        }
    }
    if ($outcome->{status} eq 'pass') {
        pass('expected to run successfully, and it did');
        ok(@result,"recieved a response");
        my $serialized_params = $json->encode($params);
        my $serialized_result = $json->encode(\@result);
        ok($serialized_params eq $serialized_result,"response matches input parameters");
        if ($serialized_params ne $serialized_result) {
            print "\nin:  ".$serialized_params."\n";
            print "out: ".$serialized_result."\n";
        }
        
    }
}





done_testing();






