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
NAME
      test-client -- list available modules and types

DESCRIPTION

      -h, --help         display this help message, ignore all arguments
      
";
      
# first parse options to get the testconfig file
my $tests_filename;
my $client_module;
my $endpoint;

my $help;

my $opt = GetOptions (
        "tests=s" => \$tests_filename,
        "module=s" => \$client_module,
        "endpoint=s" => \$endpoint,
        "help|h" => \$help,
        );

# client module must be defined
if (!$client_module) {
    fail("client module parameter must be defined");
    done_testing();
    exit 1;
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

# make sure we can import the module
my $json = JSON->new->canonical;
use_ok($client_module);
foreach my $test (@{$tests}) {
    my $client;
    if ($test->{'auth'}) {
        #initialize with creds
    } else {
        $client = $client_module->new($endpoint);
    }
    ok(defined($client),"instantiating client");
    
    my $method  = $test->{method};
    my $params  = $test->{params};
    my $outcome = $test->{outcome};
    my $result;
    {
        no strict "refs";
        ok($client->can($method), 'method "'.$method.'" exists');
        eval { $result = $client->$method(@{$params}); };
        if($@) {
            ok($outcome->{status} eq 'fail', 'expected failure, and yes it failed');
            # could do more checks here for different failure modes
        }
    }
    if ($outcome->{status} eq 'pass') {
        pass('expected to run successfully, and it did');
        ok($result,"recieved a response");
        my $serialized_params = $json->encode($params);
        my $serialized_result = $json->encode($result);
        ok($serialized_params eq $serialized_result,"response matches input parameters");
        if ($serialized_params ne $serialized_result) {
            print "\nin:  ".$serialized_params."\n";
            print "out: ".$serialized_result."\n";
        }
        
    }
    
    
    
}





done_testing();






