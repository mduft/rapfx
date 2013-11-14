RAP-FX
======

The Eclispe RAP JavaFX Client.

Please note that currently RAP does not (out of the box) support additional clients.
(see https://bugs.eclipse.org/bugs/show_bug.cgi?id=404144 ).

To get the client connected to an arbitrary RAP application, currently there is a
"workaround" required, which is more of a hack... Please see the files in the
org.rapfx.server.test plugins for a sample.

![Guess which one is in RAP-FX :)](/misc/rapfx.png)
![Another try?](/misc/rapfx2.png)

Setting up the workspace(s)
===========================

You need two workspaces, one for the server, one for the client. Start by cloning
the repository to some location on disc.

Then get an Eclipse installation that has EGit installed. Start up eclipse pointing
to the server workspace directory. Open the GIT Repositories view, and add the repository.
Right click the repo and select Import Projects ... and import the org.rapfx.server.test
project.

Go to Window -> Preferences -> Target Platform. Create a new target platform consisting
/only/ (!) of the RAP target components (fex. http://download.eclipse.org/rt/rap/2.1)
Click apply, and ok. The workspace should build now.

Right click the launch configuration in the project and run it. This should give already
the demo application in an eclipse internal browser.

For the client, start another eclipse (can be the same eclipse, only different workspace).
Again, open the Repositories view, add the same repository, and import the client project.

Go to Window -> Preferences -> installed jres and click "edit..." on your (hopefully oracle
jdk 7) vm; click add external jars, and select jfxrt.jar from the jdks "jre/lib" directory.

after clicking ok, the workspace should compile, and launching the launch config in the
plugin directory should give you the "same" application as from the server's workspace's
browser.

