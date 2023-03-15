@echo off

IF NOT EXIST tkStrikeGen2.exe (
  echo Le patch n'est pas dans le bon dossier
	goto end
)
copy /Y patch\tkStrikeGen2.cfg.original app\tkStrikeGen2.cfg
del app\lib\tkStrike-patch-crm.jar

echo Unpatched

:end
pause
