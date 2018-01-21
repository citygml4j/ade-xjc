@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Windows start script for ade-xjc example
@rem
@rem ##########################################################################

set OUTPUT="src-gen"
set PACKAGE="ade.sub.jaxb"
set BINDING="binding.xjb"
set SCHEMA="CityGML-SubsurfaceADE-0_9_0.xsd"

call ..\bin\ade-xjc.bat -clean -output %OUTPUT% -package %PACKAGE% -binding %BINDING% %SCHEMA%