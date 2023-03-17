# TK-Strike from Daedo

Comptability with [OVR](https://set.sportdata.org/wp/2014/08/19/306/)


## Write a script for :

### TODO

- Update database for athletes (unecessary)


### DONE

- Update database for nodes : judge and competitors sensors
- Update rules:
   - differential score to 5



## Shortcuts

See `com.xtremis.daedo.tkstrike.ui.TkStrikeKeyCombinationsHelper`

### Main

| shortcut        | comments |
|-----------------|----------|
| `Ctrl+C`      | Network configuration |
| `Ctrl+M`      | Match / Ring manager |
| `Ctrl+N`      | Next match |
| `Ctrl+P`      | Previous match |
| `Ctrl+F`      | Final result |
| `Ctrl+T`      | Hardware test |
| `Ctrl+K`      | Kye Shi |
| `Ctrl+D`      | Doctor |
| `Ctrl+Alt+E` | External screen |
| `Ctrl+Alt+J` | Show backup system (hidden) |
| `Alt+B`        | Scoreboard editor / video request by blue |
| `Alt+B`        | Scoreboard editor / video request by red |
| `Shift+Ctrl+Alt+D` | Enable & show differential score |
| `Shift+Ctrl+Alt+D` | Disabled & show differential score |
| `Enter`         | Start round |
| `Space`         | Pause / resume time out  |
| `K`             | Open CRM configuration if patched |


### Round working / pause mode

| shortcut        | comments |
|-----------------|----------|
| `Ctrl+1`        | Add blue 2 points for blue  |
| `Ctrl+2`        | Add blue 2 points for blue  |
| `Ctrl+3`        | Add blue 3 points for blue |
| `Ctrl+4`        | Add blue 2 points for blue |
| `Ctrl+5`        | Add blue 1 point for blue |
| `Ctrl+6`        | Add blue 2 points for red |
| `Ctrl+7`        | Add blue 2 points for red |
| `Ctrl+8`        | Add blue 3 points for red |
| `Ctrl+9`        | Add blue 2 points for red |
| `Ctrl+0`        | Add blue 1 point for red |
| `Ctrl+Shift+1`| Remove blue 2 points for blue |
| `Ctrl+Shift+2`| Remove blue 2 points for blue |
| `Ctrl+Shift+3`| Remove blue 3 points for blue |
| `Ctrl+Shift+4`| Remove blue 2 points for blue |
| `Ctrl+Shift+5`| Remove blue 1 points for blue |
| `Ctrl+Shift+6`| Remove blue 2 points for red |
| `Ctrl+Shift+7`| Remove blue 2 points for red |
| `Ctrl+Shift+8`| Remove blue 3 points for red |
| `Ctrl+Shift+9`| Remove blue 2 points for red |
| `Ctrl+Shift+0`| Remove blue 1 points for red |
| `B`             | Add Gam Jeom for blue |
| `Ctrl+Shift+B` | Remove Gam Jeom for blue |
| `R`             | Add Gam Jeom for red |
| `Ctrl+Shift+R` | Remove Gam Jeom for red |


### Network configuration

| shortcut        | comments |
|-----------------|----------|
| `Ctrl+Alt+S` | Toggle communication between 'Normal' & 'Simulator' (hidden) |
| `Ctrl+Alt+N` | Not allow error network on match (hidden) |
| `Ctrl+Alt+G` | Graphical details (hidden) |


### Network configuration (if properties `tkStrike.allowGroupSelectionByColor = true`, default is `false`)

| shortcut        | comments |
|-----------------|----------|
| `Ctrl+Shift+Alt+PadNum1` | Select blue group 1 |
| `Ctrl+Shift+Alt+PadNum2` | Select blue group 2 |
| `Ctrl+Shift+Alt+PadNum3` | Select blue group 3 |
| `Ctrl+Shift+Alt+PadNum4` | Select blue group 4 |
| `Ctrl+Shift+Alt+PadNum5` | Select blue group 5 |
| `Ctrl+Shift+Alt+PadNum6` | Select blue group 6 |
| `Ctrl+Shift+Alt+Q` | Select red group 1 |
| `Ctrl+Shift+Alt+W` | Select red group 2 |
| `Ctrl+Shift+Alt+E` | Select red group 3 |
| `Ctrl+Shift+Alt+R` | Select red group 4 |
| `Ctrl+Shift+Alt+T` | Select red group 5 |
| `Ctrl+Shift+Alt+Y` | Select red group 6 |


### Scoreboard

| shortcut        | comments |
|-----------------|----------|
| `Enter`       | Apply changes and close the scoreboard |
| `Esc`          | Cancel |
| `Ctrl+Alt+G` | Graphical details (hidden) |
| `Ctrl+1`        | Add blue 2 points for blue  |
| `Ctrl+2`        | Add blue 2 points for blue  |
| `Ctrl+3`        | Add blue 3 points for blue |
| `Ctrl+4`        | Add blue 2 points for blue |
| `Ctrl+5`        | Add blue 1 point for blue |
| `Ctrl+6`        | Add blue 2 points for red |
| `Ctrl+7`        | Add blue 2 points for red |
| `Ctrl+8`        | Add blue 3 points for red |
| `Ctrl+9`        | Add blue 2 points for red |
| `Ctrl+0`        | Add blue 1 point for red |
| `Ctrl+Shift+1`| Remove blue 2 points for blue |
| `Ctrl+Shift+2`| Remove blue 2 points for blue |
| `Ctrl+Shift+3`| Remove blue 3 points for blue |
| `Ctrl+Shift+4`| Remove blue 2 points for blue |
| `Ctrl+Shift+5`| Remove blue 1 points for blue |
| `Ctrl+Shift+6`| Remove blue 2 points for red |
| `Ctrl+Shift+7`| Remove blue 2 points for red |
| `Ctrl+Shift+8`| Remove blue 3 points for red |
| `Ctrl+Shift+9`| Remove blue 2 points for red |
| `Ctrl+Shift+0`| Remove blue 1 points for red |
| `B`             | Add Gam Jeom for blue |
| `Ctrl+Shift+B` | Remove Gam Jeom for blue |
| `R`             | Add Gam Jeom for red |
| `Ctrl+Shift+R` | Remove Gam Jeom for red |


### External screen

| shortcut        | comments |
|-----------------|----------|
| `Ctrl+Alt+E` | Exit full screen |



## Update software

Gen 1:

```
http://94.23.215.81:8080/tkStrike-software-updates-webapp/api/secure/tkStrike-newRules2017/software-updates/has-new-version?version={currentVersion}&build={currentBuild}
http://94.23.215.81:8080/tkStrike-software-updates-webapp/api/secure/tkStrike-newRules2017/software-updates/download-release/{release-id}
```

Gen 2:

```
http://94.23.215.81:8080/tkStrike-software-updates-webapp/api/secure/tkStrike-gen2-newRules2017/software-updates/has-new-version?version={currentVersion}&build={currentBuild}
http://94.23.215.81:8080/tkStrike-software-updates-webapp/api/secure/tkStrike-gen2-newRules2017/software-updates/download-release/{release-id}
```
> user: tkStrikeApp
>
> password: tkStrikeAppPassword

Example:

```
http://94.23.215.81:8080/tkStrike-software-updates-webapp/api/secure/tkStrike-gen2-newRules2017/software-updates/has-new-version?version=3.2.4-RELEASE&build=202211161145
```


## Database H2


### Server web :

```
java -cp app/lib/h2-1.4.199.jar org.h2.tools.Server -baseDir app\db
```

> Pilote JDBC: org.h2.Driver
>
> URL JDBC: jdbc:h2:file:./tkStrike30
>
> User Name: SA
>
> Password: 


### Shell :

```
java -cp app/lib/h2-1.4.199.jar org.h2.tools.Shell -url "jdbc:h2:file:./db/tkStrike30" -user SA
```

