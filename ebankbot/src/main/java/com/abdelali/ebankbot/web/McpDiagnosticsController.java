package com.abdelali.ebankbot.web;

import java.util.Arrays;
import java.util.List;

import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("dev")
public class McpDiagnosticsController {

    private final SyncMcpToolCallbackProvider mcpTools;

    public McpDiagnosticsController(
            SyncMcpToolCallbackProvider mcpTools) {
        this.mcpTools = mcpTools;
    }

    @GetMapping("/dev/mcp-tools")
    public List<String> listTools() {
        return Arrays.stream(mcpTools.getToolCallbacks())
                .map(tool -> tool.getToolDefinition().name())
                .toList();
    }
}