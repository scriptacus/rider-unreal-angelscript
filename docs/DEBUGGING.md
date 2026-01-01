# Debugging AngelScript in Rider

This guide explains how to set up and use debugging for AngelScript code in your Unreal Engine project.

## Features

- ✅ **Breakpoints** - Set breakpoints by clicking in the gutter
- ✅ **Step Debugging** - Step over, into, and out of functions
- ✅ **Variable Inspection** - Inspect locals, globals, and member variables
- ✅ **Debug Inline Values** - See variable values displayed inline during debugging
- ✅ **Call Stack** - Navigate the execution stack
- ✅ **Exception Breakpoints** - Break on assertions, errors, or warnings
- ✅ **Expression Evaluation** - Evaluate AngelScript expressions while paused
- ❌ **Data Breakpoints** - Not supported (LSP4IJ framework limitation - missing DataBreakpointInfo/SetDataBreakpoints requests)

## Prerequisites

1. **Node.js** installed on your system (any recent version)
2. **Unreal Engine** with the AngelScript plugin running
3. Your project's `Script` folder opened in Rider

## Setup Debugging

### Step 1: Create a Debug Configuration

1. Go to **Run → Edit Configurations** (or click the dropdown next to the Run button)
2. Click the **+** button and select **Debug Adapter Protocol**
3. Give it a name like "Debug AngelScript"

### Step 2: Save the Configuration

No additional configuration should be necessary.

Click **OK** to save your debug configuration.

## Using the Debugger
> Note: Keys may differ based on IDE configuration.

### Setting Breakpoints

1. Open any `.as` file in your project
2. Click in the **gutter** (left margin) next to a line of code
3. A red dot should appear indicating a breakpoint

### Starting a Debug Session

1. Make sure **Unreal Engine is running** with your project loaded
2. Select your debug configuration from the dropdown (top-right)
3. Click the **Debug** button (or press **Shift+F9**)
   >Note: Clicking the **Run** button will appear to connect the debugger, but debugging in this state will not work.
4. The debugger will connect to Unreal Engine

### When a Breakpoint Hits

When your code reaches a breakpoint:
- ⏸️ **Execution pauses** in both Unreal Engine and Rider
- 📊 **Variables panel** shows local variables, `this`, and globals
- 📚 **Call Stack panel** shows the execution stack
- 🎮 **Debug toolbar** allows you to step through code

### Stepping Through Code

- **F8** - Step Over (execute current line, don't enter functions)
- **F7** - Step Into (enter function calls)
- **Shift+F8** - Step Out (finish current function and return to caller)
- **F9** - Resume execution (continue until next breakpoint)

### Inspecting Variables

- Hover over any variable to see its value
- Expand objects in the Variables panel to see members
- Right-click variables for more options (Add to Watches, etc.)

### Evaluating Expressions

- Use the **Evaluate Expression** dialog (Alt+F8) to execute AngelScript code
- Type any expression and see the result
- You can call functions, access properties, etc.

## Advanced Features

## Concurrent Debugging (C++ + AngelScript)

You can debug C++ and AngelScript **simultaneously**:

1. Attach Rider's C++ debugger to the Unreal Editor process
2. Start an AngelScript debug session (as described above)
3. Both debuggers work independently:
   - C++ debugger controls the native process
   - AngelScript debugger communicates with the script VM via TCP

This allows you to:
- Set breakpoints in both C++ and AngelScript code
- Step from C++ into AngelScript and vice versa
- See the full execution flow across native and script code

## Troubleshooting

### "Node.js executable not found"

**Problem**: Debug adapter can't start

**Solution**: Install Node.js from https://nodejs.org/ or ensure it's in your PATH

### "Connection refused" or Timeout

**Problem**: Can't connect to Unreal Engine

**Solutions**:
1. Make sure Unreal Engine is **running** with your project loaded
2. Verify the port is 27099 (default) or check your project settings
3. Check if a firewall is blocking the connection
4. Verify the AngelScript plugin is enabled in Unreal

### Breakpoints Not Hitting

**Problem**: Breakpoints are set but execution doesn't stop

**Solutions**:
1. Make sure the debug session is **active** (green indicator in toolbar)
2. Verify the code is actually being executed (add a log statement)
3. Check that the file path matches between Rider and Unreal
4. Set **Working Directory** to your Unreal project root

### Variables Show as "Unavailable"

**Problem**: Can't inspect variable values

**Solutions**:
1. Make sure you're paused at a breakpoint (not just stopped)
2. Try stepping to the next line
3. Some variables might be optimized away by the AngelScript compiler

## Configuration Options

### Custom Port

The debug port is configurable in the plugin settings:

1. Go to **Settings → Languages & Frameworks → AngelScript → General**
2. Change **Unreal Connection Port** (default: 27099)
3. Restart your debug session for changes to take effect

> **Note**: The port must match the debug server port configured in your Unreal Engine project.

### Multiple Unreal Instances

To debug multiple Unreal instances simultaneously:

1. Configure each Unreal instance to use a different debug port
2. In Rider settings, set the port to match your target instance
3. Each debug session will connect to the port specified in settings

## Known Limitations

1. **No hot reload**: Changing code during debugging requires restarting the session
2. **Data breakpoints**: Not supported due to LSP4IJ framework limitations (missing DAP protocol support)
3. **Conditional breakpoints**: Not yet supported (standard DAP feature, may be added in future LSP4IJ updates)

## Additional Resources

- [AngelScript Language Documentation](https://angelscript.hazelight.se)
- [Unreal Engine AngelScript Plugin](https://github.com/Hazelight/UnrealEngine-Angelscript)
- [Debug Adapter Protocol Specification](https://microsoft.github.io/debug-adapter-protocol/)

## Support

If you encounter issues:

1. Check the **Event Log** in Rider (View → Tool Windows → Event Log)
2. Enable verbose logging for the debug adapter (edit configuration → trace: true)
3. Report issues at: https://github.com/scriptacus/rider-unreal-angelscript/issues
