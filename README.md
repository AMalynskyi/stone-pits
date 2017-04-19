# StonePits
Game with Stones &amp; Pits

You can play it here https://stone-pits.herokuapp.com/

## Used libraries and engines 

1) LibGDX - core game engine
2) Artemis ODB - entities engine
3) VisEditor - scene design
4) JUnit, AssertJ - testing
5) Java 8 
6) Gradle - build and assembling 

## Sources content:

1) Core engine sources 
https://github.com/AMalynskyi/stone-pits/tree/master/core

2) Web GWT sources and louncher 
https://github.com/AMalynskyi/stone-pits/tree/master/html

3) Desktop version launcher
https://github.com/AMalynskyi/stone-pits/tree/master/desktop

4) VIS Editor project sources for game scenes compositions
https://github.com/AMalynskyi/stone-pits/tree/master/vis

5) WEB deployment available here
https://github.com/AMalynskyi/stone-pits/tree/master/html/build/libs/stone-pits.war

6) Game engine testing
https://github.com/AMalynskyi/stone-pits/tree/master/core/test

## Design Patterns

### Strategy Behavioral Design Pattern
Classes:
BaseSceneManagerStrategy
GameSceneManagerStrategy

### Chain Of Responsibility Object Behavioral Design Pattern
Classes:
GamePlayRuler
RulesChainNode
GrabOppositeStonesRule
OneMoreTurnRule
GameOverRule

### State Behavioral Design Pattern
Classes:
GameStateHandler
MenuStateHandler
PlayStateHandler
StartedStateHandler

### Factory Class Design Pattern
Classes:
SceneManagerFactory

### Singleton Design Pattern
Classes:
StonePits

## Assembly

Using Gradle there are following options to build project.

### Desktop version

To build desktop deployment execute:
gradlew clean desktop:dist

Find executable jar here:
./desktop/build/libs/stone-pits-1.1.jar

### GWT version

To build WAR deployment execute:
gradlew clean html:war

Find deployment at:
./html/build/libs/stone-pits.war

### Tests

Because of Integration tests use lambda expressions execution requires Java 8.

Run Unit tests by:
gradlew clean core:test

Run Integration test by:
gradlew clean core:testIntegration

Run All tests by:
gradlew clean core:testAll

Find reports separately for Unit and Integration tests in dir:
./core/build/reports/tests

### Java DOCs

Run JavaDocs by:
gradlew core:javadoc

Find docs in:
./core/build/docs/javadoc/index.html

