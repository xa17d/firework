copy ServiceXvsm\mozartspaces-dist-2.3-SNAPSHOT-r14098-all-with-dependencies.jar build\libs\mozartspaces-dist-2.3-SNAPSHOT-r14098-all-with-dependencies.jar /Y
cd build\libs\
java -cp mozartspaces-dist-2.3-SNAPSHOT-r14098-all-with-dependencies.jar;ServiceInterface.jar org.mozartspaces.core.Server 9876