=== Build ===

gradle build			Kompiliert die Source Files
bin/copyFromBuild.bat	Aktualisiert bin Ordner mit neu kompilierten jar-Files

=== Ausführen ===

Im bin Ordner befinden sich alle jar-Files. Die Skripts zum Ausführen befinden sich in den Ordnern xvsm und alt, je nachdem ob die XVSM Implementierung oder die Alternativ Implementierung verwendet werden soll.
In diesen Ordnern existieren:
start-server-*.jar			Starten den Server
start-gui-*.bat				Startet die GUI
start-manufacturer-*.bat	Startet einen neuen Produzenten
start-qualitycontroller-*.bat	Starten einen neuen Qualitätskontrolleur
start-logistican-*.bat		Startet einen neuen Logistiker
start-ordermanager-*.bat	Startet einen Order-Manager (Wenn eine Bestellung aufgegeben wird, muss ein Order-Manager laufen)
start-customer-10000-*.bat	Startet einen Customer mit ID 10000
start-customer-20000-*.bat	Startet einen Customer mit ID 20000