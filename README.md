# Clarty
# Introduktion
Hej allesammans!

De flesta av er har säkert hört talas om ett projekt som vi ska försöka dra igång inom CAD, nämligen Clarty.

Tanken är i vilket fall att det skulle vara kul att ha ett gemensamt projekt för oss, där vi kan få experimentera med nya tekniker, ramverk och sånt som gör en glad! Vi har en sån otroligt bred kunskap tillsammans att jag tror att om vi gör ett hyfsat brett projekt tillsammans så kommer vi kunna lära varandra och ha väldigt roligt samtidigt!

Så idéen till projektet är att göra en sida och/eller app för att ersätta nulägets analoga drinkbiljetter vi har på våra konferenser och middagar! Sen finns det såklart rum för att lägga till mer funktionalitet allt eftersom, men det känns som en bra start att börja från!

Så vi behöver ha en sida för användare där man kan logga in, signa upp, få fram sina biljetter, dela med sig eller använda dem
en annan del är administratörssidan, där en admin ska kunna skapa biljetter och dela ut till användarna

Allt om projektet är självklart valfritt, man deltar vid intresse och när man har tid!

Men en tanke är att vi försöker dela upp systemet i microservicar, bara för att lättare kunna experimentera och se hur man kan göra på olika sätt!

Så systemet skulle förslagsvis kunna vara uppdelad på det här sättet:

* Frontend: Angular2
* Backend för inloggning
* Backend för administratörstjänster
* Backend för användartjänster

Sen kan vi få in nåt CI-system (jenkins/gocd/travis/ eller nåt annat) som bygger och deployar ut våra releaser till en cloud-provider (digital ocean/aws/google cloud eller liknande) och där kan vi även snöa in oss på hur man bygger organiserade server-kluster (docker-swarm/kubernetes/mesos)

Annat smått och gott vi kan leka med för projektet är
* Sonar      (Automatisk kodanalys)
* Sentry     (Larmsystem som ser om någon användare har fått ett fel)
* ELK-stack (Ett sätt att visualisera och söka strukturerat i loggar)
* Docker     (Virtualiserade miljöer för varje service)

I github finns det ett ganska bra stöd för att skapa issues där man kan lägga upp frågor osv.

Vi är fortfarande otroligt mycket i uppstartsfasen av det hela, så ifall ni har några idéer eller tankar så är allt välkommet!

### How to

Har dockerifierat applikationen.
Och för att det ska fungera så behöver man installera docker och docker-compose

på windows finns det docker-toolbox som installerar allt åt en
på unix så får man installera docker, och sen docker-compose separat :)

```bash
# För att köra docker (disabla enhetstesten eftersom de inte fungerar just nu)
mvn clean install -Pdocker -DskipTests

# Starta applikationen i docker containrar (user-services och postgres) (-d lägger processerna i bakgrunden)
docker-compose up -d

# För att följa loggarna
docker-compose logs -f

# För att stänga av applikationen
docker-compose kill
```