# Anwendungsorientierte Programmierung (C680) – Qwirkle
## Gruppenmitglieder
- Bennet Freigang
- Jarosh Hanke
- Alex Giffy

## Allgemeine Informationen
- Java 21, Eclipse 2025-12, **Swing-Pflicht**
- Objektorientiert, klare Trennung Logik/GUI (MVP- oder MVVM-Muster)
- Debug-Modus verpflichtend (freies Platzieren von Steinen/Würfeln etc.)
- Gruppengröße: 3 Studierende
- **Deckblatt-Deadline:** 08.08.2026, 12:00 Uhr
- **Projekt-Abgabe:** 14.09.2026, 12:00 Uhr

## [Qwirkle](./Qwirkle.pdf)

Qwirkle ist ein Strategiespiel für zwei bis vier Spieler. Die Spieler legen Spielsteine mit unterschiedlichen Farben und Symbolen auf einem gemeinsamen Spielfeld ab. Ziel des Spiels ist es, möglichst viele Punkte zu erzielen, indem Reihen gleicher Farbe oder gleichen Symbols gebildet werden.

## Git-Workflow

- **main** ist geschützt – kein direkter Push, nur per Pull Request
- Arbeit erfolgt auf eigenen Branches
- Vor dem Merge:
  - Mind. 1 Review durch ein Teammitglied
  - Build-Workflow muss erfolgreich durchlaufen
- Merge-Strategie: **Squash Merge** (ein sauberer Commit pro Feature)
- Commit-Nachrichten kurz & aussagekräftig (z. B. `feat: Spielbrett-Logik hinzugefügt`)
