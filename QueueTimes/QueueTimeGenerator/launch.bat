python ./Tool/MainModule.py "\\ohlewnas0260\Reporting & Analytics\LUCZAKC\3b. Content\EZ Path\processedPageFiles" 2015-03 2015-04

@echo off
if not exist %~dp0output mkdir %~dp0output
echo Done. Press a key to close this window...
@echo off
pause >nul