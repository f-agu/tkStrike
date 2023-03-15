@echo off

IF NOT EXIST tkStrikeGen2.exe (
  echo Le patch n'est pas dans le bon dossier
	goto end
)
copy /Y patch\tkStrikeGen2.cfg.patched app\tkStrikeGen2.cfg
copy /Y patch\tkStrike-patch-crm.jar app\lib\.

echo Patched

:end
pause
