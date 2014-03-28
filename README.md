flowexit
========

A scala app for manipulating - and removing - content from FlowDock.

Currently I just let Eclipse compile the classes and 
run this from command line like this:

~/scala/scala-2.10.3/bin/scala -cp bin flowexit.<MainClass> < email > < password > < org > < flow > > output.txt

Program status goes to STDERR and URL list goes to STDOUT.

There are now several programs that can be run with the same signature:

ExtractUrls
- list URLs from the flow

FindMe
- output users from the flow

FindMyMessages
- output everything written by me

MessagesHighScores
- output a sorted table of emails and message counts
