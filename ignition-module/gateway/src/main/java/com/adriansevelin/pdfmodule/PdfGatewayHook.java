package com.adriansevelin.pdfmodule;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

public class PdfGatewayHook extends AbstractGatewayModuleHook 
{
    @Override
    public void setup(GatewayContext context) 
    {

    }

    @Override
    public void shutdown() 
    {

    }

    @Override
    public void startup(LicenseState activationState) 
    {
        
    }

    @Override
    public void initializeScriptManager(ScriptManager manager) 
    {
        manager.addScriptModule("system.pdf", new PdfScriptFunctions());
    }
}
