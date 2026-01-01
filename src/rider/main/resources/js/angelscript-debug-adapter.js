var __create = Object.create;
var __defProp = Object.defineProperty;
var __getOwnPropDesc = Object.getOwnPropertyDescriptor;
var __getOwnPropNames = Object.getOwnPropertyNames;
var __getProtoOf = Object.getPrototypeOf;
var __hasOwnProp = Object.prototype.hasOwnProperty;
var __commonJS = (cb, mod) => function __require() {
  return mod || (0, cb[__getOwnPropNames(cb)[0]])((mod = { exports: {} }).exports, mod), mod.exports;
};
var __copyProps = (to, from, except, desc) => {
  if (from && typeof from === "object" || typeof from === "function") {
    for (let key of __getOwnPropNames(from))
      if (!__hasOwnProp.call(to, key) && key !== except)
        __defProp(to, key, { get: () => from[key], enumerable: !(desc = __getOwnPropDesc(from, key)) || desc.enumerable });
  }
  return to;
};
var __toESM = (mod, isNodeMode, target) => (target = mod != null ? __create(__getProtoOf(mod)) : {}, __copyProps(
  isNodeMode || !mod || !mod.__esModule ? __defProp(target, "default", { value: mod, enumerable: true }) : target,
  mod
));

// third-party/vscode-unreal-angelscript/extension/node_modules/vscode-debugadapter/lib/messages.js
var require_messages = __commonJS({
  "third-party/vscode-unreal-angelscript/extension/node_modules/vscode-debugadapter/lib/messages.js"(exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.Event = exports.Response = exports.Message = void 0;
    var Message2 = class {
      constructor(type) {
        this.seq = 0;
        this.type = type;
      }
    };
    exports.Message = Message2;
    var Response = class extends Message2 {
      constructor(request, message) {
        super("response");
        this.request_seq = request.seq;
        this.command = request.command;
        if (message) {
          this.success = false;
          this.message = message;
        } else {
          this.success = true;
        }
      }
    };
    exports.Response = Response;
    var Event = class extends Message2 {
      constructor(event, body) {
        super("event");
        this.event = event;
        if (body) {
          this.body = body;
        }
      }
    };
    exports.Event = Event;
  }
});

// third-party/vscode-unreal-angelscript/extension/node_modules/vscode-debugadapter/lib/protocol.js
var require_protocol = __commonJS({
  "third-party/vscode-unreal-angelscript/extension/node_modules/vscode-debugadapter/lib/protocol.js"(exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.ProtocolServer = void 0;
    var ee = require("events");
    var messages_1 = require_messages();
    var Emitter = class {
      get event() {
        if (!this._event) {
          this._event = (listener, thisArg) => {
            this._listener = listener;
            this._this = thisArg;
            let result;
            result = {
              dispose: () => {
                this._listener = void 0;
                this._this = void 0;
              }
            };
            return result;
          };
        }
        return this._event;
      }
      fire(event) {
        if (this._listener) {
          try {
            this._listener.call(this._this, event);
          } catch (e) {
          }
        }
      }
      hasListener() {
        return !!this._listener;
      }
      dispose() {
        this._listener = void 0;
        this._this = void 0;
      }
    };
    var ProtocolServer = class extends ee.EventEmitter {
      constructor() {
        super();
        this._sendMessage = new Emitter();
        this._pendingRequests = /* @__PURE__ */ new Map();
        this.onDidSendMessage = this._sendMessage.event;
      }
      dispose() {
      }
      handleMessage(msg) {
        if (msg.type === "request") {
          this.dispatchRequest(msg);
        } else if (msg.type === "response") {
          const response = msg;
          const clb = this._pendingRequests.get(response.request_seq);
          if (clb) {
            this._pendingRequests.delete(response.request_seq);
            clb(response);
          }
        }
      }
      _isRunningInline() {
        return this._sendMessage && this._sendMessage.hasListener();
      }
      start(inStream, outStream) {
        this._sequence = 1;
        this._writableStream = outStream;
        this._rawData = Buffer.alloc(0);
        inStream.on("data", (data) => this._handleData(data));
        inStream.on("close", () => {
          this._emitEvent(new messages_1.Event("close"));
        });
        inStream.on("error", (error) => {
          this._emitEvent(new messages_1.Event("error", "inStream error: " + (error && error.message)));
        });
        outStream.on("error", (error) => {
          this._emitEvent(new messages_1.Event("error", "outStream error: " + (error && error.message)));
        });
        inStream.resume();
      }
      stop() {
        if (this._writableStream) {
          this._writableStream.end();
        }
      }
      sendEvent(event) {
        this._send("event", event);
      }
      sendResponse(response) {
        if (response.seq > 0) {
          console.error(`attempt to send more than one response for command ${response.command}`);
        } else {
          this._send("response", response);
        }
      }
      sendRequest(command, args, timeout, cb) {
        const request = {
          command
        };
        if (args && Object.keys(args).length > 0) {
          request.arguments = args;
        }
        this._send("request", request);
        if (cb) {
          this._pendingRequests.set(request.seq, cb);
          const timer = setTimeout(() => {
            clearTimeout(timer);
            const clb = this._pendingRequests.get(request.seq);
            if (clb) {
              this._pendingRequests.delete(request.seq);
              clb(new messages_1.Response(request, "timeout"));
            }
          }, timeout);
        }
      }
      dispatchRequest(request) {
      }
      _emitEvent(event) {
        this.emit(event.event, event);
      }
      _send(typ, message) {
        message.type = typ;
        message.seq = this._sequence++;
        if (this._writableStream) {
          const json = JSON.stringify(message);
          this._writableStream.write(`Content-Length: ${Buffer.byteLength(json, "utf8")}\r
\r
${json}`, "utf8");
        }
        this._sendMessage.fire(message);
      }
      _handleData(data) {
        this._rawData = Buffer.concat([this._rawData, data]);
        while (true) {
          if (this._contentLength >= 0) {
            if (this._rawData.length >= this._contentLength) {
              const message = this._rawData.toString("utf8", 0, this._contentLength);
              this._rawData = this._rawData.slice(this._contentLength);
              this._contentLength = -1;
              if (message.length > 0) {
                try {
                  let msg = JSON.parse(message);
                  this.handleMessage(msg);
                } catch (e) {
                  this._emitEvent(new messages_1.Event("error", "Error handling data: " + (e && e.message)));
                }
              }
              continue;
            }
          } else {
            const idx = this._rawData.indexOf(ProtocolServer.TWO_CRLF);
            if (idx !== -1) {
              const header = this._rawData.toString("utf8", 0, idx);
              const lines = header.split("\r\n");
              for (let i = 0; i < lines.length; i++) {
                const pair = lines[i].split(/: +/);
                if (pair[0] == "Content-Length") {
                  this._contentLength = +pair[1];
                }
              }
              this._rawData = this._rawData.slice(idx + ProtocolServer.TWO_CRLF.length);
              continue;
            }
          }
          break;
        }
      }
    };
    exports.ProtocolServer = ProtocolServer;
    ProtocolServer.TWO_CRLF = "\r\n\r\n";
  }
});

// third-party/vscode-unreal-angelscript/extension/node_modules/vscode-debugadapter/lib/runDebugAdapter.js
var require_runDebugAdapter = __commonJS({
  "third-party/vscode-unreal-angelscript/extension/node_modules/vscode-debugadapter/lib/runDebugAdapter.js"(exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.runDebugAdapter = void 0;
    var Net = require("net");
    function runDebugAdapter(debugSession) {
      let port = 0;
      const args = process.argv.slice(2);
      args.forEach(function(val, index, array) {
        const portMatch = /^--server=(\d{4,5})$/.exec(val);
        if (portMatch) {
          port = parseInt(portMatch[1], 10);
        }
      });
      if (port > 0) {
        console.error(`waiting for debug protocol on port ${port}`);
        Net.createServer((socket) => {
          console.error(">> accepted connection from client");
          socket.on("end", () => {
            console.error(">> client connection closed\n");
          });
          const session = new debugSession(false, true);
          session.setRunAsServer(true);
          session.start(socket, socket);
        }).listen(port);
      } else {
        const session = new debugSession(false);
        process.on("SIGTERM", () => {
          session.shutdown();
        });
        session.start(process.stdin, process.stdout);
      }
    }
    exports.runDebugAdapter = runDebugAdapter;
  }
});

// third-party/vscode-unreal-angelscript/extension/node_modules/vscode-debugadapter/lib/debugSession.js
var require_debugSession = __commonJS({
  "third-party/vscode-unreal-angelscript/extension/node_modules/vscode-debugadapter/lib/debugSession.js"(exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.DebugSession = exports.ErrorDestination = exports.InvalidatedEvent = exports.ProgressEndEvent = exports.ProgressUpdateEvent = exports.ProgressStartEvent = exports.CapabilitiesEvent = exports.LoadedSourceEvent = exports.ModuleEvent = exports.BreakpointEvent = exports.ThreadEvent = exports.OutputEvent = exports.ExitedEvent = exports.TerminatedEvent = exports.InitializedEvent = exports.ContinuedEvent = exports.StoppedEvent = exports.CompletionItem = exports.Module = exports.Breakpoint = exports.Variable = exports.Thread = exports.StackFrame = exports.Scope = exports.Source = void 0;
    var protocol_1 = require_protocol();
    var messages_1 = require_messages();
    var runDebugAdapter_1 = require_runDebugAdapter();
    var url_1 = require("url");
    var Source2 = class {
      constructor(name, path2, id = 0, origin, data) {
        this.name = name;
        this.path = path2;
        this.sourceReference = id;
        if (origin) {
          this.origin = origin;
        }
        if (data) {
          this.adapterData = data;
        }
      }
    };
    exports.Source = Source2;
    var Scope2 = class {
      constructor(name, reference, expensive = false) {
        this.name = name;
        this.variablesReference = reference;
        this.expensive = expensive;
      }
    };
    exports.Scope = Scope2;
    var StackFrame2 = class {
      constructor(i, nm, src, ln = 0, col = 0) {
        this.id = i;
        this.source = src;
        this.line = ln;
        this.column = col;
        this.name = nm;
      }
    };
    exports.StackFrame = StackFrame2;
    var Thread2 = class {
      constructor(id, name) {
        this.id = id;
        if (name) {
          this.name = name;
        } else {
          this.name = "Thread #" + id;
        }
      }
    };
    exports.Thread = Thread2;
    var Variable = class {
      constructor(name, value, ref = 0, indexedVariables, namedVariables) {
        this.name = name;
        this.value = value;
        this.variablesReference = ref;
        if (typeof namedVariables === "number") {
          this.namedVariables = namedVariables;
        }
        if (typeof indexedVariables === "number") {
          this.indexedVariables = indexedVariables;
        }
      }
    };
    exports.Variable = Variable;
    var Breakpoint2 = class {
      constructor(verified, line, column, source) {
        this.verified = verified;
        const e = this;
        if (typeof line === "number") {
          e.line = line;
        }
        if (typeof column === "number") {
          e.column = column;
        }
        if (source) {
          e.source = source;
        }
      }
      setId(id) {
        this.id = id;
      }
    };
    exports.Breakpoint = Breakpoint2;
    var Module = class {
      constructor(id, name) {
        this.id = id;
        this.name = name;
      }
    };
    exports.Module = Module;
    var CompletionItem = class {
      constructor(label, start, length = 0) {
        this.label = label;
        this.start = start;
        this.length = length;
      }
    };
    exports.CompletionItem = CompletionItem;
    var StoppedEvent2 = class extends messages_1.Event {
      constructor(reason, threadId, exceptionText) {
        super("stopped");
        this.body = {
          reason
        };
        if (typeof threadId === "number") {
          this.body.threadId = threadId;
        }
        if (typeof exceptionText === "string") {
          this.body.text = exceptionText;
        }
      }
    };
    exports.StoppedEvent = StoppedEvent2;
    var ContinuedEvent2 = class extends messages_1.Event {
      constructor(threadId, allThreadsContinued) {
        super("continued");
        this.body = {
          threadId
        };
        if (typeof allThreadsContinued === "boolean") {
          this.body.allThreadsContinued = allThreadsContinued;
        }
      }
    };
    exports.ContinuedEvent = ContinuedEvent2;
    var InitializedEvent2 = class extends messages_1.Event {
      constructor() {
        super("initialized");
      }
    };
    exports.InitializedEvent = InitializedEvent2;
    var TerminatedEvent2 = class extends messages_1.Event {
      constructor(restart) {
        super("terminated");
        if (typeof restart === "boolean" || restart) {
          const e = this;
          e.body = {
            restart
          };
        }
      }
    };
    exports.TerminatedEvent = TerminatedEvent2;
    var ExitedEvent = class extends messages_1.Event {
      constructor(exitCode) {
        super("exited");
        this.body = {
          exitCode
        };
      }
    };
    exports.ExitedEvent = ExitedEvent;
    var OutputEvent2 = class extends messages_1.Event {
      constructor(output, category = "console", data) {
        super("output");
        this.body = {
          category,
          output
        };
        if (data !== void 0) {
          this.body.data = data;
        }
      }
    };
    exports.OutputEvent = OutputEvent2;
    var ThreadEvent = class extends messages_1.Event {
      constructor(reason, threadId) {
        super("thread");
        this.body = {
          reason,
          threadId
        };
      }
    };
    exports.ThreadEvent = ThreadEvent;
    var BreakpointEvent2 = class extends messages_1.Event {
      constructor(reason, breakpoint) {
        super("breakpoint");
        this.body = {
          reason,
          breakpoint
        };
      }
    };
    exports.BreakpointEvent = BreakpointEvent2;
    var ModuleEvent = class extends messages_1.Event {
      constructor(reason, module3) {
        super("module");
        this.body = {
          reason,
          module: module3
        };
      }
    };
    exports.ModuleEvent = ModuleEvent;
    var LoadedSourceEvent = class extends messages_1.Event {
      constructor(reason, source) {
        super("loadedSource");
        this.body = {
          reason,
          source
        };
      }
    };
    exports.LoadedSourceEvent = LoadedSourceEvent;
    var CapabilitiesEvent = class extends messages_1.Event {
      constructor(capabilities) {
        super("capabilities");
        this.body = {
          capabilities
        };
      }
    };
    exports.CapabilitiesEvent = CapabilitiesEvent;
    var ProgressStartEvent = class extends messages_1.Event {
      constructor(progressId, title, message) {
        super("progressStart");
        this.body = {
          progressId,
          title
        };
        if (typeof message === "string") {
          this.body.message = message;
        }
      }
    };
    exports.ProgressStartEvent = ProgressStartEvent;
    var ProgressUpdateEvent = class extends messages_1.Event {
      constructor(progressId, message) {
        super("progressUpdate");
        this.body = {
          progressId
        };
        if (typeof message === "string") {
          this.body.message = message;
        }
      }
    };
    exports.ProgressUpdateEvent = ProgressUpdateEvent;
    var ProgressEndEvent = class extends messages_1.Event {
      constructor(progressId, message) {
        super("progressEnd");
        this.body = {
          progressId
        };
        if (typeof message === "string") {
          this.body.message = message;
        }
      }
    };
    exports.ProgressEndEvent = ProgressEndEvent;
    var InvalidatedEvent = class extends messages_1.Event {
      constructor(areas, threadId, stackFrameId) {
        super("invalidated");
        this.body = {};
        if (areas) {
          this.body.areas = areas;
        }
        if (threadId) {
          this.body.threadId = threadId;
        }
        if (stackFrameId) {
          this.body.stackFrameId = stackFrameId;
        }
      }
    };
    exports.InvalidatedEvent = InvalidatedEvent;
    var ErrorDestination;
    (function(ErrorDestination2) {
      ErrorDestination2[ErrorDestination2["User"] = 1] = "User";
      ErrorDestination2[ErrorDestination2["Telemetry"] = 2] = "Telemetry";
    })(ErrorDestination = exports.ErrorDestination || (exports.ErrorDestination = {}));
    var DebugSession = class extends protocol_1.ProtocolServer {
      constructor(obsolete_debuggerLinesAndColumnsStartAt1, obsolete_isServer) {
        super();
        const linesAndColumnsStartAt1 = typeof obsolete_debuggerLinesAndColumnsStartAt1 === "boolean" ? obsolete_debuggerLinesAndColumnsStartAt1 : false;
        this._debuggerLinesStartAt1 = linesAndColumnsStartAt1;
        this._debuggerColumnsStartAt1 = linesAndColumnsStartAt1;
        this._debuggerPathsAreURIs = false;
        this._clientLinesStartAt1 = true;
        this._clientColumnsStartAt1 = true;
        this._clientPathsAreURIs = false;
        this._isServer = typeof obsolete_isServer === "boolean" ? obsolete_isServer : false;
        this.on("close", () => {
          this.shutdown();
        });
        this.on("error", (error) => {
          this.shutdown();
        });
      }
      setDebuggerPathFormat(format) {
        this._debuggerPathsAreURIs = format !== "path";
      }
      setDebuggerLinesStartAt1(enable) {
        this._debuggerLinesStartAt1 = enable;
      }
      setDebuggerColumnsStartAt1(enable) {
        this._debuggerColumnsStartAt1 = enable;
      }
      setRunAsServer(enable) {
        this._isServer = enable;
      }
      static run(debugSession) {
        (0, runDebugAdapter_1.runDebugAdapter)(debugSession);
      }
      shutdown() {
        if (this._isServer || this._isRunningInline()) {
        } else {
          setTimeout(() => {
            process.exit(0);
          }, 100);
        }
      }
      sendErrorResponse(response, codeOrMessage, format, variables, dest = ErrorDestination.User) {
        let msg;
        if (typeof codeOrMessage === "number") {
          msg = {
            id: codeOrMessage,
            format
          };
          if (variables) {
            msg.variables = variables;
          }
          if (dest & ErrorDestination.User) {
            msg.showUser = true;
          }
          if (dest & ErrorDestination.Telemetry) {
            msg.sendTelemetry = true;
          }
        } else {
          msg = codeOrMessage;
        }
        response.success = false;
        response.message = DebugSession.formatPII(msg.format, true, msg.variables);
        if (!response.body) {
          response.body = {};
        }
        response.body.error = msg;
        this.sendResponse(response);
      }
      runInTerminalRequest(args, timeout, cb) {
        this.sendRequest("runInTerminal", args, timeout, cb);
      }
      dispatchRequest(request) {
        const response = new messages_1.Response(request);
        try {
          if (request.command === "initialize") {
            var args = request.arguments;
            if (typeof args.linesStartAt1 === "boolean") {
              this._clientLinesStartAt1 = args.linesStartAt1;
            }
            if (typeof args.columnsStartAt1 === "boolean") {
              this._clientColumnsStartAt1 = args.columnsStartAt1;
            }
            if (args.pathFormat !== "path") {
              this.sendErrorResponse(response, 2018, "debug adapter only supports native paths", null, ErrorDestination.Telemetry);
            } else {
              const initializeResponse = response;
              initializeResponse.body = {};
              this.initializeRequest(initializeResponse, args);
            }
          } else if (request.command === "launch") {
            this.launchRequest(response, request.arguments, request);
          } else if (request.command === "attach") {
            this.attachRequest(response, request.arguments, request);
          } else if (request.command === "disconnect") {
            this.disconnectRequest(response, request.arguments, request);
          } else if (request.command === "terminate") {
            this.terminateRequest(response, request.arguments, request);
          } else if (request.command === "restart") {
            this.restartRequest(response, request.arguments, request);
          } else if (request.command === "setBreakpoints") {
            this.setBreakPointsRequest(response, request.arguments, request);
          } else if (request.command === "setFunctionBreakpoints") {
            this.setFunctionBreakPointsRequest(response, request.arguments, request);
          } else if (request.command === "setExceptionBreakpoints") {
            this.setExceptionBreakPointsRequest(response, request.arguments, request);
          } else if (request.command === "configurationDone") {
            this.configurationDoneRequest(response, request.arguments, request);
          } else if (request.command === "continue") {
            this.continueRequest(response, request.arguments, request);
          } else if (request.command === "next") {
            this.nextRequest(response, request.arguments, request);
          } else if (request.command === "stepIn") {
            this.stepInRequest(response, request.arguments, request);
          } else if (request.command === "stepOut") {
            this.stepOutRequest(response, request.arguments, request);
          } else if (request.command === "stepBack") {
            this.stepBackRequest(response, request.arguments, request);
          } else if (request.command === "reverseContinue") {
            this.reverseContinueRequest(response, request.arguments, request);
          } else if (request.command === "restartFrame") {
            this.restartFrameRequest(response, request.arguments, request);
          } else if (request.command === "goto") {
            this.gotoRequest(response, request.arguments, request);
          } else if (request.command === "pause") {
            this.pauseRequest(response, request.arguments, request);
          } else if (request.command === "stackTrace") {
            this.stackTraceRequest(response, request.arguments, request);
          } else if (request.command === "scopes") {
            this.scopesRequest(response, request.arguments, request);
          } else if (request.command === "variables") {
            this.variablesRequest(response, request.arguments, request);
          } else if (request.command === "setVariable") {
            this.setVariableRequest(response, request.arguments, request);
          } else if (request.command === "setExpression") {
            this.setExpressionRequest(response, request.arguments, request);
          } else if (request.command === "source") {
            this.sourceRequest(response, request.arguments, request);
          } else if (request.command === "threads") {
            this.threadsRequest(response, request);
          } else if (request.command === "terminateThreads") {
            this.terminateThreadsRequest(response, request.arguments, request);
          } else if (request.command === "evaluate") {
            this.evaluateRequest(response, request.arguments, request);
          } else if (request.command === "stepInTargets") {
            this.stepInTargetsRequest(response, request.arguments, request);
          } else if (request.command === "gotoTargets") {
            this.gotoTargetsRequest(response, request.arguments, request);
          } else if (request.command === "completions") {
            this.completionsRequest(response, request.arguments, request);
          } else if (request.command === "exceptionInfo") {
            this.exceptionInfoRequest(response, request.arguments, request);
          } else if (request.command === "loadedSources") {
            this.loadedSourcesRequest(response, request.arguments, request);
          } else if (request.command === "dataBreakpointInfo") {
            this.dataBreakpointInfoRequest(response, request.arguments, request);
          } else if (request.command === "setDataBreakpoints") {
            this.setDataBreakpointsRequest(response, request.arguments, request);
          } else if (request.command === "readMemory") {
            this.readMemoryRequest(response, request.arguments, request);
          } else if (request.command === "writeMemory") {
            this.writeMemoryRequest(response, request.arguments, request);
          } else if (request.command === "disassemble") {
            this.disassembleRequest(response, request.arguments, request);
          } else if (request.command === "cancel") {
            this.cancelRequest(response, request.arguments, request);
          } else if (request.command === "breakpointLocations") {
            this.breakpointLocationsRequest(response, request.arguments, request);
          } else if (request.command === "setInstructionBreakpoints") {
            this.setInstructionBreakpointsRequest(response, request.arguments, request);
          } else {
            this.customRequest(request.command, response, request.arguments, request);
          }
        } catch (e) {
          this.sendErrorResponse(response, 1104, "{_stack}", { _exception: e.message, _stack: e.stack }, ErrorDestination.Telemetry);
        }
      }
      initializeRequest(response, args) {
        response.body.supportsConditionalBreakpoints = false;
        response.body.supportsHitConditionalBreakpoints = false;
        response.body.supportsFunctionBreakpoints = false;
        response.body.supportsConfigurationDoneRequest = true;
        response.body.supportsEvaluateForHovers = false;
        response.body.supportsStepBack = false;
        response.body.supportsSetVariable = false;
        response.body.supportsRestartFrame = false;
        response.body.supportsStepInTargetsRequest = false;
        response.body.supportsGotoTargetsRequest = false;
        response.body.supportsCompletionsRequest = false;
        response.body.supportsRestartRequest = false;
        response.body.supportsExceptionOptions = false;
        response.body.supportsValueFormattingOptions = false;
        response.body.supportsExceptionInfoRequest = false;
        response.body.supportTerminateDebuggee = false;
        response.body.supportsDelayedStackTraceLoading = false;
        response.body.supportsLoadedSourcesRequest = false;
        response.body.supportsLogPoints = false;
        response.body.supportsTerminateThreadsRequest = false;
        response.body.supportsSetExpression = false;
        response.body.supportsTerminateRequest = false;
        response.body.supportsDataBreakpoints = false;
        response.body.supportsReadMemoryRequest = false;
        response.body.supportsDisassembleRequest = false;
        response.body.supportsCancelRequest = false;
        response.body.supportsBreakpointLocationsRequest = false;
        response.body.supportsClipboardContext = false;
        response.body.supportsSteppingGranularity = false;
        response.body.supportsInstructionBreakpoints = false;
        response.body.supportsExceptionFilterOptions = false;
        this.sendResponse(response);
      }
      disconnectRequest(response, args, request) {
        this.sendResponse(response);
        this.shutdown();
      }
      launchRequest(response, args, request) {
        this.sendResponse(response);
      }
      attachRequest(response, args, request) {
        this.sendResponse(response);
      }
      terminateRequest(response, args, request) {
        this.sendResponse(response);
      }
      restartRequest(response, args, request) {
        this.sendResponse(response);
      }
      setBreakPointsRequest(response, args, request) {
        this.sendResponse(response);
      }
      setFunctionBreakPointsRequest(response, args, request) {
        this.sendResponse(response);
      }
      setExceptionBreakPointsRequest(response, args, request) {
        this.sendResponse(response);
      }
      configurationDoneRequest(response, args, request) {
        this.sendResponse(response);
      }
      continueRequest(response, args, request) {
        this.sendResponse(response);
      }
      nextRequest(response, args, request) {
        this.sendResponse(response);
      }
      stepInRequest(response, args, request) {
        this.sendResponse(response);
      }
      stepOutRequest(response, args, request) {
        this.sendResponse(response);
      }
      stepBackRequest(response, args, request) {
        this.sendResponse(response);
      }
      reverseContinueRequest(response, args, request) {
        this.sendResponse(response);
      }
      restartFrameRequest(response, args, request) {
        this.sendResponse(response);
      }
      gotoRequest(response, args, request) {
        this.sendResponse(response);
      }
      pauseRequest(response, args, request) {
        this.sendResponse(response);
      }
      sourceRequest(response, args, request) {
        this.sendResponse(response);
      }
      threadsRequest(response, request) {
        this.sendResponse(response);
      }
      terminateThreadsRequest(response, args, request) {
        this.sendResponse(response);
      }
      stackTraceRequest(response, args, request) {
        this.sendResponse(response);
      }
      scopesRequest(response, args, request) {
        this.sendResponse(response);
      }
      variablesRequest(response, args, request) {
        this.sendResponse(response);
      }
      setVariableRequest(response, args, request) {
        this.sendResponse(response);
      }
      setExpressionRequest(response, args, request) {
        this.sendResponse(response);
      }
      evaluateRequest(response, args, request) {
        this.sendResponse(response);
      }
      stepInTargetsRequest(response, args, request) {
        this.sendResponse(response);
      }
      gotoTargetsRequest(response, args, request) {
        this.sendResponse(response);
      }
      completionsRequest(response, args, request) {
        this.sendResponse(response);
      }
      exceptionInfoRequest(response, args, request) {
        this.sendResponse(response);
      }
      loadedSourcesRequest(response, args, request) {
        this.sendResponse(response);
      }
      dataBreakpointInfoRequest(response, args, request) {
        this.sendResponse(response);
      }
      setDataBreakpointsRequest(response, args, request) {
        this.sendResponse(response);
      }
      readMemoryRequest(response, args, request) {
        this.sendResponse(response);
      }
      writeMemoryRequest(response, args, request) {
        this.sendResponse(response);
      }
      disassembleRequest(response, args, request) {
        this.sendResponse(response);
      }
      cancelRequest(response, args, request) {
        this.sendResponse(response);
      }
      breakpointLocationsRequest(response, args, request) {
        this.sendResponse(response);
      }
      setInstructionBreakpointsRequest(response, args, request) {
        this.sendResponse(response);
      }
      customRequest(command, response, args, request) {
        this.sendErrorResponse(response, 1014, "unrecognized request", null, ErrorDestination.Telemetry);
      }
      convertClientLineToDebugger(line) {
        if (this._debuggerLinesStartAt1) {
          return this._clientLinesStartAt1 ? line : line + 1;
        }
        return this._clientLinesStartAt1 ? line - 1 : line;
      }
      convertDebuggerLineToClient(line) {
        if (this._debuggerLinesStartAt1) {
          return this._clientLinesStartAt1 ? line : line - 1;
        }
        return this._clientLinesStartAt1 ? line + 1 : line;
      }
      convertClientColumnToDebugger(column) {
        if (this._debuggerColumnsStartAt1) {
          return this._clientColumnsStartAt1 ? column : column + 1;
        }
        return this._clientColumnsStartAt1 ? column - 1 : column;
      }
      convertDebuggerColumnToClient(column) {
        if (this._debuggerColumnsStartAt1) {
          return this._clientColumnsStartAt1 ? column : column - 1;
        }
        return this._clientColumnsStartAt1 ? column + 1 : column;
      }
      convertClientPathToDebugger(clientPath) {
        if (this._clientPathsAreURIs !== this._debuggerPathsAreURIs) {
          if (this._clientPathsAreURIs) {
            return DebugSession.uri2path(clientPath);
          } else {
            return DebugSession.path2uri(clientPath);
          }
        }
        return clientPath;
      }
      convertDebuggerPathToClient(debuggerPath) {
        if (this._debuggerPathsAreURIs !== this._clientPathsAreURIs) {
          if (this._debuggerPathsAreURIs) {
            return DebugSession.uri2path(debuggerPath);
          } else {
            return DebugSession.path2uri(debuggerPath);
          }
        }
        return debuggerPath;
      }
      static path2uri(path2) {
        if (process.platform === "win32") {
          if (/^[A-Z]:/.test(path2)) {
            path2 = path2[0].toLowerCase() + path2.substr(1);
          }
          path2 = path2.replace(/\\/g, "/");
        }
        path2 = encodeURI(path2);
        let uri = new url_1.URL(`file:`);
        uri.pathname = path2;
        return uri.toString();
      }
      static uri2path(sourceUri) {
        let uri = new url_1.URL(sourceUri);
        let s = decodeURIComponent(uri.pathname);
        if (process.platform === "win32") {
          if (/^\/[a-zA-Z]:/.test(s)) {
            s = s[1].toLowerCase() + s.substr(2);
          }
          s = s.replace(/\//g, "\\");
        }
        return s;
      }
      static formatPII(format, excludePII, args) {
        return format.replace(DebugSession._formatPIIRegexp, function(match, paramName) {
          if (excludePII && paramName.length > 0 && paramName[0] !== "_") {
            return match;
          }
          return args[paramName] && args.hasOwnProperty(paramName) ? args[paramName] : match;
        });
      }
    };
    exports.DebugSession = DebugSession;
    DebugSession._formatPIIRegexp = /{([^}]+)}/g;
  }
});

// third-party/vscode-unreal-angelscript/extension/node_modules/mkdirp/lib/opts-arg.js
var require_opts_arg = __commonJS({
  "third-party/vscode-unreal-angelscript/extension/node_modules/mkdirp/lib/opts-arg.js"(exports, module2) {
    var { promisify } = require("util");
    var fs2 = require("fs");
    var optsArg = (opts) => {
      if (!opts)
        opts = { mode: 511, fs: fs2 };
      else if (typeof opts === "object")
        opts = { mode: 511, fs: fs2, ...opts };
      else if (typeof opts === "number")
        opts = { mode: opts, fs: fs2 };
      else if (typeof opts === "string")
        opts = { mode: parseInt(opts, 8), fs: fs2 };
      else
        throw new TypeError("invalid options argument");
      opts.mkdir = opts.mkdir || opts.fs.mkdir || fs2.mkdir;
      opts.mkdirAsync = promisify(opts.mkdir);
      opts.stat = opts.stat || opts.fs.stat || fs2.stat;
      opts.statAsync = promisify(opts.stat);
      opts.statSync = opts.statSync || opts.fs.statSync || fs2.statSync;
      opts.mkdirSync = opts.mkdirSync || opts.fs.mkdirSync || fs2.mkdirSync;
      return opts;
    };
    module2.exports = optsArg;
  }
});

// third-party/vscode-unreal-angelscript/extension/node_modules/mkdirp/lib/path-arg.js
var require_path_arg = __commonJS({
  "third-party/vscode-unreal-angelscript/extension/node_modules/mkdirp/lib/path-arg.js"(exports, module2) {
    var platform = process.env.__TESTING_MKDIRP_PLATFORM__ || process.platform;
    var { resolve, parse } = require("path");
    var pathArg = (path2) => {
      if (/\0/.test(path2)) {
        throw Object.assign(
          new TypeError("path must be a string without null bytes"),
          {
            path: path2,
            code: "ERR_INVALID_ARG_VALUE"
          }
        );
      }
      path2 = resolve(path2);
      if (platform === "win32") {
        const badWinChars = /[*|"<>?:]/;
        const { root } = parse(path2);
        if (badWinChars.test(path2.substr(root.length))) {
          throw Object.assign(new Error("Illegal characters in path."), {
            path: path2,
            code: "EINVAL"
          });
        }
      }
      return path2;
    };
    module2.exports = pathArg;
  }
});

// third-party/vscode-unreal-angelscript/extension/node_modules/mkdirp/lib/find-made.js
var require_find_made = __commonJS({
  "third-party/vscode-unreal-angelscript/extension/node_modules/mkdirp/lib/find-made.js"(exports, module2) {
    var { dirname } = require("path");
    var findMade = (opts, parent, path2 = void 0) => {
      if (path2 === parent)
        return Promise.resolve();
      return opts.statAsync(parent).then(
        (st) => st.isDirectory() ? path2 : void 0,
        (er) => er.code === "ENOENT" ? findMade(opts, dirname(parent), parent) : void 0
      );
    };
    var findMadeSync = (opts, parent, path2 = void 0) => {
      if (path2 === parent)
        return void 0;
      try {
        return opts.statSync(parent).isDirectory() ? path2 : void 0;
      } catch (er) {
        return er.code === "ENOENT" ? findMadeSync(opts, dirname(parent), parent) : void 0;
      }
    };
    module2.exports = { findMade, findMadeSync };
  }
});

// third-party/vscode-unreal-angelscript/extension/node_modules/mkdirp/lib/mkdirp-manual.js
var require_mkdirp_manual = __commonJS({
  "third-party/vscode-unreal-angelscript/extension/node_modules/mkdirp/lib/mkdirp-manual.js"(exports, module2) {
    var { dirname } = require("path");
    var mkdirpManual = (path2, opts, made) => {
      opts.recursive = false;
      const parent = dirname(path2);
      if (parent === path2) {
        return opts.mkdirAsync(path2, opts).catch((er) => {
          if (er.code !== "EISDIR")
            throw er;
        });
      }
      return opts.mkdirAsync(path2, opts).then(() => made || path2, (er) => {
        if (er.code === "ENOENT")
          return mkdirpManual(parent, opts).then((made2) => mkdirpManual(path2, opts, made2));
        if (er.code !== "EEXIST" && er.code !== "EROFS")
          throw er;
        return opts.statAsync(path2).then((st) => {
          if (st.isDirectory())
            return made;
          else
            throw er;
        }, () => {
          throw er;
        });
      });
    };
    var mkdirpManualSync = (path2, opts, made) => {
      const parent = dirname(path2);
      opts.recursive = false;
      if (parent === path2) {
        try {
          return opts.mkdirSync(path2, opts);
        } catch (er) {
          if (er.code !== "EISDIR")
            throw er;
          else
            return;
        }
      }
      try {
        opts.mkdirSync(path2, opts);
        return made || path2;
      } catch (er) {
        if (er.code === "ENOENT")
          return mkdirpManualSync(path2, opts, mkdirpManualSync(parent, opts, made));
        if (er.code !== "EEXIST" && er.code !== "EROFS")
          throw er;
        try {
          if (!opts.statSync(path2).isDirectory())
            throw er;
        } catch (_) {
          throw er;
        }
      }
    };
    module2.exports = { mkdirpManual, mkdirpManualSync };
  }
});

// third-party/vscode-unreal-angelscript/extension/node_modules/mkdirp/lib/mkdirp-native.js
var require_mkdirp_native = __commonJS({
  "third-party/vscode-unreal-angelscript/extension/node_modules/mkdirp/lib/mkdirp-native.js"(exports, module2) {
    var { dirname } = require("path");
    var { findMade, findMadeSync } = require_find_made();
    var { mkdirpManual, mkdirpManualSync } = require_mkdirp_manual();
    var mkdirpNative = (path2, opts) => {
      opts.recursive = true;
      const parent = dirname(path2);
      if (parent === path2)
        return opts.mkdirAsync(path2, opts);
      return findMade(opts, path2).then((made) => opts.mkdirAsync(path2, opts).then(() => made).catch((er) => {
        if (er.code === "ENOENT")
          return mkdirpManual(path2, opts);
        else
          throw er;
      }));
    };
    var mkdirpNativeSync = (path2, opts) => {
      opts.recursive = true;
      const parent = dirname(path2);
      if (parent === path2)
        return opts.mkdirSync(path2, opts);
      const made = findMadeSync(opts, path2);
      try {
        opts.mkdirSync(path2, opts);
        return made;
      } catch (er) {
        if (er.code === "ENOENT")
          return mkdirpManualSync(path2, opts);
        else
          throw er;
      }
    };
    module2.exports = { mkdirpNative, mkdirpNativeSync };
  }
});

// third-party/vscode-unreal-angelscript/extension/node_modules/mkdirp/lib/use-native.js
var require_use_native = __commonJS({
  "third-party/vscode-unreal-angelscript/extension/node_modules/mkdirp/lib/use-native.js"(exports, module2) {
    var fs2 = require("fs");
    var version = process.env.__TESTING_MKDIRP_NODE_VERSION__ || process.version;
    var versArr = version.replace(/^v/, "").split(".");
    var hasNative = +versArr[0] > 10 || +versArr[0] === 10 && +versArr[1] >= 12;
    var useNative = !hasNative ? () => false : (opts) => opts.mkdir === fs2.mkdir;
    var useNativeSync = !hasNative ? () => false : (opts) => opts.mkdirSync === fs2.mkdirSync;
    module2.exports = { useNative, useNativeSync };
  }
});

// third-party/vscode-unreal-angelscript/extension/node_modules/mkdirp/index.js
var require_mkdirp = __commonJS({
  "third-party/vscode-unreal-angelscript/extension/node_modules/mkdirp/index.js"(exports, module2) {
    var optsArg = require_opts_arg();
    var pathArg = require_path_arg();
    var { mkdirpNative, mkdirpNativeSync } = require_mkdirp_native();
    var { mkdirpManual, mkdirpManualSync } = require_mkdirp_manual();
    var { useNative, useNativeSync } = require_use_native();
    var mkdirp = (path2, opts) => {
      path2 = pathArg(path2);
      opts = optsArg(opts);
      return useNative(opts) ? mkdirpNative(path2, opts) : mkdirpManual(path2, opts);
    };
    var mkdirpSync = (path2, opts) => {
      path2 = pathArg(path2);
      opts = optsArg(opts);
      return useNativeSync(opts) ? mkdirpNativeSync(path2, opts) : mkdirpManualSync(path2, opts);
    };
    mkdirp.sync = mkdirpSync;
    mkdirp.native = (path2, opts) => mkdirpNative(pathArg(path2), optsArg(opts));
    mkdirp.manual = (path2, opts) => mkdirpManual(pathArg(path2), optsArg(opts));
    mkdirp.nativeSync = (path2, opts) => mkdirpNativeSync(pathArg(path2), optsArg(opts));
    mkdirp.manualSync = (path2, opts) => mkdirpManualSync(pathArg(path2), optsArg(opts));
    module2.exports = mkdirp;
  }
});

// third-party/vscode-unreal-angelscript/extension/node_modules/vscode-debugadapter/lib/internalLogger.js
var require_internalLogger = __commonJS({
  "third-party/vscode-unreal-angelscript/extension/node_modules/vscode-debugadapter/lib/internalLogger.js"(exports) {
    "use strict";
    var __awaiter = exports && exports.__awaiter || function(thisArg, _arguments, P, generator) {
      function adopt(value) {
        return value instanceof P ? value : new P(function(resolve) {
          resolve(value);
        });
      }
      return new (P || (P = Promise))(function(resolve, reject) {
        function fulfilled(value) {
          try {
            step(generator.next(value));
          } catch (e) {
            reject(e);
          }
        }
        function rejected(value) {
          try {
            step(generator["throw"](value));
          } catch (e) {
            reject(e);
          }
        }
        function step(result) {
          result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected);
        }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
      });
    };
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.InternalLogger = void 0;
    var fs2 = require("fs");
    var path2 = require("path");
    var mkdirp = require_mkdirp();
    var logger_1 = require_logger();
    var InternalLogger = class {
      constructor(logCallback, isServer) {
        this.beforeExitCallback = () => this.dispose();
        this._logCallback = logCallback;
        this._logToConsole = isServer;
        this._minLogLevel = logger_1.LogLevel.Warn;
        this.disposeCallback = (signal, code) => {
          this.dispose();
          code = code || 2;
          code += 128;
          process.exit(code);
        };
      }
      setup(options) {
        return __awaiter(this, void 0, void 0, function* () {
          this._minLogLevel = options.consoleMinLogLevel;
          this._prependTimestamp = options.prependTimestamp;
          if (options.logFilePath) {
            if (!path2.isAbsolute(options.logFilePath)) {
              this.log(`logFilePath must be an absolute path: ${options.logFilePath}`, logger_1.LogLevel.Error);
            } else {
              const handleError = (err) => this.sendLog(`Error creating log file at path: ${options.logFilePath}. Error: ${err.toString()}
`, logger_1.LogLevel.Error);
              try {
                yield mkdirp(path2.dirname(options.logFilePath));
                this.log(`Verbose logs are written to:
`, logger_1.LogLevel.Warn);
                this.log(options.logFilePath + "\n", logger_1.LogLevel.Warn);
                this._logFileStream = fs2.createWriteStream(options.logFilePath);
                this.logDateTime();
                this.setupShutdownListeners();
                this._logFileStream.on("error", (err) => {
                  handleError(err);
                });
              } catch (err) {
                handleError(err);
              }
            }
          }
        });
      }
      logDateTime() {
        let d = new Date();
        let dateString = d.getUTCFullYear() + `-${d.getUTCMonth() + 1}-` + d.getUTCDate();
        const timeAndDateStamp = dateString + ", " + getFormattedTimeString();
        this.log(timeAndDateStamp + "\n", logger_1.LogLevel.Verbose, false);
      }
      setupShutdownListeners() {
        process.addListener("beforeExit", this.beforeExitCallback);
        process.addListener("SIGTERM", this.disposeCallback);
        process.addListener("SIGINT", this.disposeCallback);
      }
      removeShutdownListeners() {
        process.removeListener("beforeExit", this.beforeExitCallback);
        process.removeListener("SIGTERM", this.disposeCallback);
        process.removeListener("SIGINT", this.disposeCallback);
      }
      dispose() {
        return new Promise((resolve) => {
          this.removeShutdownListeners();
          if (this._logFileStream) {
            this._logFileStream.end(resolve);
            this._logFileStream = null;
          } else {
            resolve();
          }
        });
      }
      log(msg, level, prependTimestamp = true) {
        if (this._minLogLevel === logger_1.LogLevel.Stop) {
          return;
        }
        if (level >= this._minLogLevel) {
          this.sendLog(msg, level);
        }
        if (this._logToConsole) {
          const logFn = level === logger_1.LogLevel.Error ? console.error : level === logger_1.LogLevel.Warn ? console.warn : null;
          if (logFn) {
            logFn((0, logger_1.trimLastNewline)(msg));
          }
        }
        if (level === logger_1.LogLevel.Error) {
          msg = `[${logger_1.LogLevel[level]}] ${msg}`;
        }
        if (this._prependTimestamp && prependTimestamp) {
          msg = "[" + getFormattedTimeString() + "] " + msg;
        }
        if (this._logFileStream) {
          this._logFileStream.write(msg);
        }
      }
      sendLog(msg, level) {
        if (msg.length > 1500) {
          const endsInNewline = !!msg.match(/(\n|\r\n)$/);
          msg = msg.substr(0, 1500) + "[...]";
          if (endsInNewline) {
            msg = msg + "\n";
          }
        }
        if (this._logCallback) {
          const event = new logger_1.LogOutputEvent(msg, level);
          this._logCallback(event);
        }
      }
    };
    exports.InternalLogger = InternalLogger;
    function getFormattedTimeString() {
      let d = new Date();
      let hourString = _padZeroes(2, String(d.getUTCHours()));
      let minuteString = _padZeroes(2, String(d.getUTCMinutes()));
      let secondString = _padZeroes(2, String(d.getUTCSeconds()));
      let millisecondString = _padZeroes(3, String(d.getUTCMilliseconds()));
      return hourString + ":" + minuteString + ":" + secondString + "." + millisecondString + " UTC";
    }
    function _padZeroes(minDesiredLength, numberToPad) {
      if (numberToPad.length >= minDesiredLength) {
        return numberToPad;
      } else {
        return String("0".repeat(minDesiredLength) + numberToPad).slice(-minDesiredLength);
      }
    }
  }
});

// third-party/vscode-unreal-angelscript/extension/node_modules/vscode-debugadapter/lib/logger.js
var require_logger = __commonJS({
  "third-party/vscode-unreal-angelscript/extension/node_modules/vscode-debugadapter/lib/logger.js"(exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.trimLastNewline = exports.LogOutputEvent = exports.logger = exports.Logger = exports.LogLevel = void 0;
    var internalLogger_1 = require_internalLogger();
    var debugSession_1 = require_debugSession();
    var LogLevel;
    (function(LogLevel2) {
      LogLevel2[LogLevel2["Verbose"] = 0] = "Verbose";
      LogLevel2[LogLevel2["Log"] = 1] = "Log";
      LogLevel2[LogLevel2["Warn"] = 2] = "Warn";
      LogLevel2[LogLevel2["Error"] = 3] = "Error";
      LogLevel2[LogLevel2["Stop"] = 4] = "Stop";
    })(LogLevel = exports.LogLevel || (exports.LogLevel = {}));
    var Logger2 = class {
      constructor() {
        this._pendingLogQ = [];
      }
      log(msg, level = LogLevel.Log) {
        msg = msg + "\n";
        this._write(msg, level);
      }
      verbose(msg) {
        this.log(msg, LogLevel.Verbose);
      }
      warn(msg) {
        this.log(msg, LogLevel.Warn);
      }
      error(msg) {
        this.log(msg, LogLevel.Error);
      }
      dispose() {
        if (this._currentLogger) {
          const disposeP = this._currentLogger.dispose();
          this._currentLogger = null;
          return disposeP;
        } else {
          return Promise.resolve();
        }
      }
      _write(msg, level = LogLevel.Log) {
        msg = msg + "";
        if (this._pendingLogQ) {
          this._pendingLogQ.push({ msg, level });
        } else if (this._currentLogger) {
          this._currentLogger.log(msg, level);
        }
      }
      setup(consoleMinLogLevel, _logFilePath, prependTimestamp = true) {
        const logFilePath = typeof _logFilePath === "string" ? _logFilePath : _logFilePath && this._logFilePathFromInit;
        if (this._currentLogger) {
          const options = {
            consoleMinLogLevel,
            logFilePath,
            prependTimestamp
          };
          this._currentLogger.setup(options).then(() => {
            if (this._pendingLogQ) {
              const logQ = this._pendingLogQ;
              this._pendingLogQ = null;
              logQ.forEach((item) => this._write(item.msg, item.level));
            }
          });
        }
      }
      init(logCallback, logFilePath, logToConsole) {
        this._pendingLogQ = this._pendingLogQ || [];
        this._currentLogger = new internalLogger_1.InternalLogger(logCallback, logToConsole);
        this._logFilePathFromInit = logFilePath;
      }
    };
    exports.Logger = Logger2;
    exports.logger = new Logger2();
    var LogOutputEvent = class extends debugSession_1.OutputEvent {
      constructor(msg, level) {
        const category = level === LogLevel.Error ? "stderr" : level === LogLevel.Warn ? "console" : "stdout";
        super(msg, category);
      }
    };
    exports.LogOutputEvent = LogOutputEvent;
    function trimLastNewline(str) {
      return str.replace(/(\n|\r\n)$/, "");
    }
    exports.trimLastNewline = trimLastNewline;
  }
});

// third-party/vscode-unreal-angelscript/extension/node_modules/vscode-debugadapter/lib/loggingDebugSession.js
var require_loggingDebugSession = __commonJS({
  "third-party/vscode-unreal-angelscript/extension/node_modules/vscode-debugadapter/lib/loggingDebugSession.js"(exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.LoggingDebugSession = void 0;
    var Logger2 = require_logger();
    var logger2 = Logger2.logger;
    var debugSession_1 = require_debugSession();
    var LoggingDebugSession2 = class extends debugSession_1.DebugSession {
      constructor(obsolete_logFilePath, obsolete_debuggerLinesAndColumnsStartAt1, obsolete_isServer) {
        super(obsolete_debuggerLinesAndColumnsStartAt1, obsolete_isServer);
        this.obsolete_logFilePath = obsolete_logFilePath;
        this.on("error", (event) => {
          logger2.error(event.body);
        });
      }
      start(inStream, outStream) {
        super.start(inStream, outStream);
        logger2.init((e) => this.sendEvent(e), this.obsolete_logFilePath, this._isServer);
      }
      sendEvent(event) {
        if (!(event instanceof Logger2.LogOutputEvent)) {
          let objectToLog = event;
          if (event instanceof debugSession_1.OutputEvent && event.body && event.body.data && event.body.data.doNotLogOutput) {
            delete event.body.data.doNotLogOutput;
            objectToLog = Object.assign({}, event);
            objectToLog.body = Object.assign(Object.assign({}, event.body), { output: "<output not logged>" });
          }
          logger2.verbose(`To client: ${JSON.stringify(objectToLog)}`);
        }
        super.sendEvent(event);
      }
      sendRequest(command, args, timeout, cb) {
        logger2.verbose(`To client: ${JSON.stringify(command)}(${JSON.stringify(args)}), timeout: ${timeout}`);
        super.sendRequest(command, args, timeout, cb);
      }
      sendResponse(response) {
        logger2.verbose(`To client: ${JSON.stringify(response)}`);
        super.sendResponse(response);
      }
      dispatchRequest(request) {
        logger2.verbose(`From client: ${request.command}(${JSON.stringify(request.arguments)})`);
        super.dispatchRequest(request);
      }
    };
    exports.LoggingDebugSession = LoggingDebugSession2;
  }
});

// third-party/vscode-unreal-angelscript/extension/node_modules/vscode-debugadapter/lib/handles.js
var require_handles = __commonJS({
  "third-party/vscode-unreal-angelscript/extension/node_modules/vscode-debugadapter/lib/handles.js"(exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.Handles = void 0;
    var Handles2 = class {
      constructor(startHandle) {
        this.START_HANDLE = 1e3;
        this._handleMap = /* @__PURE__ */ new Map();
        this._nextHandle = typeof startHandle === "number" ? startHandle : this.START_HANDLE;
      }
      reset() {
        this._nextHandle = this.START_HANDLE;
        this._handleMap = /* @__PURE__ */ new Map();
      }
      create(value) {
        var handle = this._nextHandle++;
        this._handleMap.set(handle, value);
        return handle;
      }
      get(handle, dflt) {
        return this._handleMap.get(handle) || dflt;
      }
    };
    exports.Handles = Handles2;
  }
});

// third-party/vscode-unreal-angelscript/extension/node_modules/vscode-debugadapter/lib/main.js
var require_main = __commonJS({
  "third-party/vscode-unreal-angelscript/extension/node_modules/vscode-debugadapter/lib/main.js"(exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.Handles = exports.Response = exports.Event = exports.ErrorDestination = exports.CompletionItem = exports.Module = exports.Source = exports.Breakpoint = exports.Variable = exports.Scope = exports.StackFrame = exports.Thread = exports.InvalidatedEvent = exports.ProgressEndEvent = exports.ProgressUpdateEvent = exports.ProgressStartEvent = exports.CapabilitiesEvent = exports.LoadedSourceEvent = exports.ModuleEvent = exports.BreakpointEvent = exports.ThreadEvent = exports.OutputEvent = exports.ContinuedEvent = exports.StoppedEvent = exports.ExitedEvent = exports.TerminatedEvent = exports.InitializedEvent = exports.logger = exports.Logger = exports.LoggingDebugSession = exports.DebugSession = void 0;
    var debugSession_1 = require_debugSession();
    Object.defineProperty(exports, "DebugSession", { enumerable: true, get: function() {
      return debugSession_1.DebugSession;
    } });
    Object.defineProperty(exports, "InitializedEvent", { enumerable: true, get: function() {
      return debugSession_1.InitializedEvent;
    } });
    Object.defineProperty(exports, "TerminatedEvent", { enumerable: true, get: function() {
      return debugSession_1.TerminatedEvent;
    } });
    Object.defineProperty(exports, "ExitedEvent", { enumerable: true, get: function() {
      return debugSession_1.ExitedEvent;
    } });
    Object.defineProperty(exports, "StoppedEvent", { enumerable: true, get: function() {
      return debugSession_1.StoppedEvent;
    } });
    Object.defineProperty(exports, "ContinuedEvent", { enumerable: true, get: function() {
      return debugSession_1.ContinuedEvent;
    } });
    Object.defineProperty(exports, "OutputEvent", { enumerable: true, get: function() {
      return debugSession_1.OutputEvent;
    } });
    Object.defineProperty(exports, "ThreadEvent", { enumerable: true, get: function() {
      return debugSession_1.ThreadEvent;
    } });
    Object.defineProperty(exports, "BreakpointEvent", { enumerable: true, get: function() {
      return debugSession_1.BreakpointEvent;
    } });
    Object.defineProperty(exports, "ModuleEvent", { enumerable: true, get: function() {
      return debugSession_1.ModuleEvent;
    } });
    Object.defineProperty(exports, "LoadedSourceEvent", { enumerable: true, get: function() {
      return debugSession_1.LoadedSourceEvent;
    } });
    Object.defineProperty(exports, "CapabilitiesEvent", { enumerable: true, get: function() {
      return debugSession_1.CapabilitiesEvent;
    } });
    Object.defineProperty(exports, "ProgressStartEvent", { enumerable: true, get: function() {
      return debugSession_1.ProgressStartEvent;
    } });
    Object.defineProperty(exports, "ProgressUpdateEvent", { enumerable: true, get: function() {
      return debugSession_1.ProgressUpdateEvent;
    } });
    Object.defineProperty(exports, "ProgressEndEvent", { enumerable: true, get: function() {
      return debugSession_1.ProgressEndEvent;
    } });
    Object.defineProperty(exports, "InvalidatedEvent", { enumerable: true, get: function() {
      return debugSession_1.InvalidatedEvent;
    } });
    Object.defineProperty(exports, "Thread", { enumerable: true, get: function() {
      return debugSession_1.Thread;
    } });
    Object.defineProperty(exports, "StackFrame", { enumerable: true, get: function() {
      return debugSession_1.StackFrame;
    } });
    Object.defineProperty(exports, "Scope", { enumerable: true, get: function() {
      return debugSession_1.Scope;
    } });
    Object.defineProperty(exports, "Variable", { enumerable: true, get: function() {
      return debugSession_1.Variable;
    } });
    Object.defineProperty(exports, "Breakpoint", { enumerable: true, get: function() {
      return debugSession_1.Breakpoint;
    } });
    Object.defineProperty(exports, "Source", { enumerable: true, get: function() {
      return debugSession_1.Source;
    } });
    Object.defineProperty(exports, "Module", { enumerable: true, get: function() {
      return debugSession_1.Module;
    } });
    Object.defineProperty(exports, "CompletionItem", { enumerable: true, get: function() {
      return debugSession_1.CompletionItem;
    } });
    Object.defineProperty(exports, "ErrorDestination", { enumerable: true, get: function() {
      return debugSession_1.ErrorDestination;
    } });
    var loggingDebugSession_1 = require_loggingDebugSession();
    Object.defineProperty(exports, "LoggingDebugSession", { enumerable: true, get: function() {
      return loggingDebugSession_1.LoggingDebugSession;
    } });
    var Logger2 = require_logger();
    exports.Logger = Logger2;
    var messages_1 = require_messages();
    Object.defineProperty(exports, "Event", { enumerable: true, get: function() {
      return messages_1.Event;
    } });
    Object.defineProperty(exports, "Response", { enumerable: true, get: function() {
      return messages_1.Response;
    } });
    var handles_1 = require_handles();
    Object.defineProperty(exports, "Handles", { enumerable: true, get: function() {
      return handles_1.Handles;
    } });
    var logger2 = Logger2.logger;
    exports.logger = logger2;
  }
});

// scripts/vscode-stub.js
var require_vscode_stub = __commonJS({
  "scripts/vscode-stub.js"(exports, module2) {
    var workspace2 = {
      getConfiguration: function(section) {
        return {
          get: function(key, defaultValue) {
            return defaultValue;
          }
        };
      },
      workspaceFolders: []
    };
    module2.exports = {
      workspace: workspace2
    };
  }
});

// third-party/vscode-unreal-angelscript/extension/node_modules/await-notify/index.js
var require_await_notify = __commonJS({
  "third-party/vscode-unreal-angelscript/extension/node_modules/await-notify/index.js"(exports) {
    function Subject2() {
      this.waiters = [];
    }
    Subject2.prototype.wait = function(timeout) {
      var self = this;
      var waiter = {};
      this.waiters.push(waiter);
      var promise = new Promise(function(resolve) {
        var resolved = false;
        waiter.resolve = function(noRemove) {
          if (resolved) {
            return;
          }
          resolved = true;
          if (waiter.timeout) {
            clearTimeout(waiter.timeout);
            waiter.timeout = null;
          }
          if (!noRemove) {
            var pos = self.waiters.indexOf(waiter);
            if (pos > -1) {
              self.waiters.splice(pos, 1);
            }
          }
          resolve();
        };
      });
      if (timeout > 0 && isFinite(timeout)) {
        waiter.timeout = setTimeout(function() {
          waiter.timeout = null;
          waiter.resolve();
        }, timeout);
      }
      return promise;
    };
    Subject2.prototype.notify = function() {
      if (this.waiters.length > 0) {
        this.waiters.pop().resolve(true);
      }
    };
    Subject2.prototype.notifyAll = function() {
      for (var i = this.waiters.length - 1; i >= 0; i--) {
        this.waiters[i].resolve(true);
      }
      this.waiters = [];
    };
    exports.Subject = Subject2;
  }
});

// third-party/vscode-unreal-angelscript/extension/src/debug.ts
var import_vscode_debugadapter = __toESM(require_main());
var import_path = require("path");
var vscode = __toESM(require_vscode_stub());
var path = __toESM(require("path"));
var fs = __toESM(require("fs"));

// third-party/vscode-unreal-angelscript/extension/src/unreal-debugclient.ts
var import_net = require("net");
var import_events = require("events");
var Message = class {
  constructor(type, offset, size, buffer) {
    this.type = type;
    this.offset = offset;
    this.buffer = buffer;
    this.size = size;
  }
  readInt() {
    let value = this.buffer.readIntLE(this.offset, 4);
    this.offset += 4;
    return value;
  }
  readByte() {
    let value = this.buffer.readInt8(this.offset);
    this.offset += 1;
    return value;
  }
  readBool() {
    return this.readInt() != 0;
  }
  readAddress() {
    let value = this.buffer.readBigUInt64LE(this.offset);
    this.offset += 8;
    return value;
  }
  readString() {
    let num = this.readInt();
    let ucs2 = num < 0;
    if (ucs2) {
      num = -num;
    }
    if (ucs2) {
      let str = this.buffer.toString("utf16le", this.offset, this.offset + num * 2);
      this.offset += num * 2;
      if (str[str.length - 1] == "\0")
        str = str.substr(0, str.length - 1);
      return str;
    } else {
      let str = this.buffer.toString("utf8", this.offset, this.offset + num);
      this.offset += num;
      if (str[str.length - 1] == "\0")
        str = str.substr(0, str.length - 1);
      return str;
    }
  }
};
function writeInt(value) {
  let newBuffer = Buffer.alloc(4);
  newBuffer.writeInt32LE(value, 0);
  return newBuffer;
}
function writeString(str) {
  let newBuffer = Buffer.alloc(4);
  newBuffer.writeInt32LE(str.length + 1, 0);
  return Buffer.concat([newBuffer, Buffer.from(str + "\0", "binary")]);
}
var pendingBuffer = Buffer.alloc(0);
function readMessages(buffer) {
  let list = [];
  let offset = 0;
  pendingBuffer = Buffer.concat([pendingBuffer, buffer]);
  while (pendingBuffer.length >= 5) {
    let offset2 = 0;
    let msglen = pendingBuffer.readUIntLE(offset2, 4);
    offset2 += 4;
    let msgtype = pendingBuffer.readInt8(offset2);
    offset2 += 1;
    if (msglen <= pendingBuffer.length - offset2) {
      list.push(new Message(msgtype, offset2, msglen, pendingBuffer));
      pendingBuffer = pendingBuffer.slice(offset2 + msglen);
    } else {
      return list;
    }
  }
  return list;
}
var unreal = null;
var connected = false;
var events = new import_events.EventEmitter();
function connect(hostname, port) {
  if (unreal != null) {
    sendDisconnect();
    unreal.destroy();
  }
  unreal = new import_net.Socket();
  connected = true;
  unreal.connect(port, hostname, function() {
  });
  unreal.on("data", function(data) {
    let messages = readMessages(data);
    for (let msg of messages) {
      if (msg.type == 8 /* CallStack */) {
        events.emit("CallStack", msg);
      } else if (msg.type == 11 /* HasStopped */) {
        events.emit("Stopped", msg);
      } else if (msg.type == 12 /* HasContinued */) {
        events.emit("Continued", msg);
      } else if (msg.type == 18 /* Variables */) {
        events.emit("Variables", msg);
      } else if (msg.type == 20 /* Evaluate */) {
        events.emit("Evaluate", msg);
      } else if (msg.type == 24 /* BreakFilters */) {
        events.emit("BreakFilters", msg);
      } else if (msg.type == 10 /* SetBreakpoint */) {
        events.emit("SetBreakpoint", msg);
      } else if (msg.type == 37 /* ClearDataBreakpoints */) {
        events.emit("ClearDataBreakpoints", msg);
      } else if (msg.type == 33 /* DebugServerVersion */) {
        events.emit("DebugServerVersion", msg);
      }
    }
  });
  unreal.on("error", function() {
    if (unreal != null) {
      unreal.destroy();
      unreal = null;
      events.emit("Closed");
    }
  });
  unreal.on("close", function() {
    if (unreal != null) {
      unreal.destroy();
      unreal = null;
      events.emit("Closed");
    }
  });
}
function disconnect() {
  sendDisconnect();
  unreal.destroy();
  unreal = null;
  connected = false;
}
function sendPause() {
  let msg = Buffer.alloc(5);
  msg.writeUInt32LE(1, 0);
  msg.writeUInt8(5 /* Pause */, 4);
  unreal.write(msg);
}
function sendContinue() {
  let msg = Buffer.alloc(5);
  msg.writeUInt32LE(1, 0);
  msg.writeUInt8(6 /* Continue */, 4);
  unreal.write(msg);
}
function sendRequestBreakFilters() {
  let msg = Buffer.alloc(5);
  msg.writeUInt32LE(1, 0);
  msg.writeUInt8(23 /* RequestBreakFilters */, 4);
  unreal.write(msg);
}
function sendRequestCallStack() {
  let msg = Buffer.alloc(5);
  msg.writeUInt32LE(1, 0);
  msg.writeUInt8(7 /* RequestCallStack */, 4);
  unreal.write(msg);
}
function sendDisconnect() {
  let msg = Buffer.alloc(5);
  msg.writeUInt32LE(1, 0);
  msg.writeUInt8(25 /* Disconnect */, 4);
  unreal.write(msg);
}
function sendStartDebugging(version) {
  let msg = Buffer.alloc(5);
  msg.writeUInt32LE(1, 0);
  msg.writeUInt8(3 /* StartDebugging */, 4);
  msg = Buffer.concat([msg, writeInt(version)]);
  msg.writeUInt32LE(msg.length - 4, 0);
  unreal.write(msg);
}
function sendStopDebugging() {
  let msg = Buffer.alloc(5);
  msg.writeUInt32LE(1, 0);
  msg.writeUInt8(4 /* StopDebugging */, 4);
  unreal.write(msg);
}
function clearBreakpoints(pathname, moduleName) {
  let msg = Buffer.alloc(5);
  msg.writeUInt8(9 /* ClearBreakpoints */, 4);
  msg = Buffer.concat([msg, writeString(pathname), writeString(moduleName)]);
  msg.writeUInt32LE(msg.length - 4, 0);
  unreal.write(msg);
}
function setBreakpoint(id, pathname, line, moduleName) {
  let head = Buffer.alloc(5);
  head.writeUInt32LE(1, 0);
  head.writeUInt8(10 /* SetBreakpoint */, 4);
  let msg = Buffer.concat([
    head,
    writeString(pathname),
    writeInt(line),
    writeInt(id),
    writeString(moduleName)
  ]);
  msg.writeUInt32LE(msg.length - 4, 0);
  unreal.write(msg);
}
function setDataBreakpoints(breakpoints) {
  let count = breakpoints.length;
  let header = Buffer.alloc(5);
  header.writeUInt32LE(1, 0);
  header.writeUInt8(36 /* SetDataBreakpoints */, 4);
  let payload = Buffer.alloc(1);
  payload.writeUInt8(count, 0);
  payload = Buffer.concat([header, payload]);
  for (let i = 0; i < count; i++) {
    const breakpoint = breakpoints[i];
    let breakpointPayload = Buffer.alloc(4 + 8 + 1 + 1 + 4);
    breakpointPayload.writeInt32LE(breakpoint.id, 0);
    breakpointPayload.writeBigUInt64LE(breakpoint.address, 4);
    breakpointPayload.writeUInt8(breakpoint.size, 4 + 8);
    breakpointPayload.writeInt8(breakpoint.hitCount, 4 + 8 + 1);
    breakpointPayload.writeInt32LE(breakpoint.cppBreakpoint ? 1 : 0, 4 + 8 + 1 + 1);
    payload = Buffer.concat([payload, breakpointPayload, writeString(breakpoint.name)]);
  }
  payload.writeUInt32LE(payload.length - 4, 0);
  unreal.write(payload);
}
function sendStepIn() {
  let msg = Buffer.alloc(5);
  msg.writeUInt32LE(1, 0);
  msg.writeUInt8(14 /* StepIn */, 4);
  unreal.write(msg);
}
function sendStepOver() {
  let msg = Buffer.alloc(5);
  msg.writeUInt32LE(1, 0);
  msg.writeUInt8(13 /* StepOver */, 4);
  unreal.write(msg);
}
function sendStepOut() {
  let msg = Buffer.alloc(5);
  msg.writeUInt32LE(1, 0);
  msg.writeUInt8(15 /* StepOut */, 4);
  unreal.write(msg);
}
function sendRequestVariables(path2) {
  let head = Buffer.alloc(5);
  head.writeUInt8(17 /* RequestVariables */, 4);
  let msg = Buffer.concat([
    head,
    writeString(path2)
  ]);
  msg.writeUInt32LE(msg.length - 4, 0);
  unreal.write(msg);
}
function sendRequestEvaluate(path2, frameId) {
  let head = Buffer.alloc(5);
  head.writeUInt8(19 /* RequestEvaluate */, 4);
  let msg = Buffer.concat([
    head,
    writeString(path2),
    writeInt(frameId)
  ]);
  msg.writeUInt32LE(msg.length - 4, 0);
  unreal.write(msg);
}
function sendBreakOptions(filters) {
  let head = Buffer.alloc(5);
  head.writeUInt8(22 /* BreakOptions */, 4);
  let parts = [head, writeInt(filters.length)];
  for (let filter of filters) {
    parts.push(writeString(filter));
  }
  let msg = Buffer.concat(parts);
  msg.writeUInt32LE(msg.length - 4, 0);
  unreal.write(msg);
}

// third-party/vscode-unreal-angelscript/extension/src/debug.ts
var { Subject } = require_await_notify();
var _ASDebugSession = class extends import_vscode_debugadapter.LoggingDebugSession {
  constructor() {
    super("angelscript-debug");
    this.breakpoints = /* @__PURE__ */ new Map();
    this.dataBreakpoints = /* @__PURE__ */ new Map();
    this.nextBreakpointId = 1;
    this.variableStore = /* @__PURE__ */ new Map();
    this._variableHandles = new import_vscode_debugadapter.Handles();
    this._configurationDone = new Subject();
    this._rootPaths = this.gatherRootPaths();
    this.hostname = "127.0.0.1";
    this.port = 27099;
    this.debugAdapterVersion = 2;
    this.debugServerVersion = 0;
    this.setDebuggerLinesStartAt1(true);
    this.setDebuggerColumnsStartAt1(true);
    events.removeAllListeners();
    events.on("CallStack", (msg) => {
      this.receiveCallStack(msg);
    });
    events.on("Stopped", (msg) => {
      this.receiveStopped(msg);
    });
    events.on("Continued", (msg) => {
      this.receiveContinued();
    });
    events.on("Variables", (msg) => {
      this.receiveVariables(msg);
    });
    events.on("Evaluate", (msg) => {
      this.receiveEvaluate(msg);
    });
    events.on("BreakFilters", (msg) => {
      this.receiveBreakFilters(msg);
    });
    events.on("Closed", () => {
      this.receiveClosed();
    });
    events.on("SetBreakpoint", (msg) => {
      this.receiveBreakpoint(msg);
    });
    events.on("ClearDataBreakpoints", (msg) => {
      this.receiveClearDataBreakpoints(msg);
    });
    events.on("DebugServerVersion", (msg) => {
      this.receiveDebugVersion(msg);
    });
  }
  initializeRequest(response, args) {
    response.body = response.body || {};
    response.body.supportsConfigurationDoneRequest = true;
    response.body.supportsEvaluateForHovers = true;
    response.body.supportsExceptionInfoRequest = true;
    response.body.supportsDataBreakpoints = true;
    connect(this.hostname, this.port);
    sendRequestBreakFilters();
    this.waitingInitializeResponse = response;
  }
  receiveBreakFilters(msg) {
    this.waitingInitializeResponse.body.exceptionBreakpointFilters = [];
    let count = msg.readInt();
    for (let i = 0; i < count; ++i) {
      let filter = msg.readString();
      let filterTitle = msg.readString();
      this.waitingInitializeResponse.body.exceptionBreakpointFilters.push(
        {
          filter,
          label: filterTitle,
          default: true
        }
      );
    }
    disconnect();
    this.sendResponse(this.waitingInitializeResponse);
    this.sendEvent(new import_vscode_debugadapter.InitializedEvent());
  }
  configurationDoneRequest(response, args) {
    super.configurationDoneRequest(response, args);
    this._configurationDone.notify();
  }
  async launchRequest(response, args) {
    import_vscode_debugadapter.logger.setup(args.trace ? import_vscode_debugadapter.Logger.LogLevel.Verbose : import_vscode_debugadapter.Logger.LogLevel.Stop, false);
    let hostname = args.hostname ?? this.hostname;
    let port = args.port !== void 0 && args.port > 0 ? args.port : this.port;
    connect(hostname, port);
    sendStartDebugging(this.debugAdapterVersion);
    for (let clientPath of this.breakpoints.keys()) {
      let breakpointList = this.getBreakpointList(clientPath);
      if (breakpointList.length != 0) {
        const debugPath = this.convertClientPathToDebugger(clientPath);
        const moduleName = this.GetModuleNameForFilepath(debugPath);
        clearBreakpoints(debugPath, moduleName);
        for (let breakpoint of breakpointList) {
          setBreakpoint(breakpoint.id, debugPath, breakpoint.line, moduleName);
        }
      }
    }
    await this._configurationDone.wait(1e3);
    this.sendResponse(response);
  }
  disconnectRequest(response, args) {
    sendStopDebugging();
    disconnect();
    this.clearAllDataBreakpoints();
    this.sendResponse(response);
  }
  getBreakpointList(path2) {
    let breakpointList = this.breakpoints.get(path2);
    if (!breakpointList) {
      breakpointList = new Array();
      this.breakpoints.set(path2, breakpointList);
    }
    return breakpointList;
  }
  setBreakPointsRequest(response, args) {
    const clientLines = args.lines || [];
    const clientPath = args.source.path;
    const debugPath = this.convertClientPathToDebugger(clientPath);
    const moduleName = this.GetModuleNameForFilepath(debugPath);
    let clientBreakpoints = new Array();
    let oldBreakpointList = this.getBreakpointList(clientPath);
    let breakpointList = new Array();
    if (connected)
      clearBreakpoints(debugPath, moduleName);
    for (let line of clientLines) {
      let id = -1;
      for (let oldBP of oldBreakpointList) {
        if (oldBP.line == line)
          id = oldBP.id;
      }
      if (id == -1)
        id = this.nextBreakpointId++;
      let clientBreak = {
        id,
        verified: true,
        line
      };
      clientBreakpoints.push(clientBreak);
      let breakpoint = { id: clientBreak.id, line };
      breakpointList.push(breakpoint);
      if (connected)
        setBreakpoint(breakpoint.id, debugPath, line, moduleName);
    }
    this.breakpoints.set(clientPath, breakpointList);
    response.body = {
      breakpoints: clientBreakpoints
    };
    this.sendResponse(response);
  }
  receiveBreakpoint(msg) {
    let filename = msg.readString();
    let line = msg.readInt();
    let id = msg.readInt();
    let breakpointList = this.getBreakpointList(filename);
    let overlapsExistingBreakpoint = false;
    for (let i = 0; i < breakpointList.length; ++i) {
      let bp = breakpointList[i];
      if (bp.id != id && bp.line == line) {
        overlapsExistingBreakpoint = true;
        break;
      }
    }
    let timeout = 1;
    let adapter = this;
    for (let i = 0; i < breakpointList.length; ++i) {
      let bp = breakpointList[i];
      if (bp.id == id) {
        if (overlapsExistingBreakpoint) {
          breakpointList.splice(i, 1);
          setTimeout(function() {
            adapter.sendEvent(new import_vscode_debugadapter.BreakpointEvent("removed", { verified: false, id: bp.id }));
          }, timeout++);
        } else if (line == -1) {
          breakpointList.splice(i, 1);
          setTimeout(function() {
            adapter.sendEvent(new import_vscode_debugadapter.BreakpointEvent("changed", { verified: false, id: bp.id, line: bp.line }));
          }, timeout++);
        } else {
          bp.line = line;
          setTimeout(function() {
            adapter.sendEvent(new import_vscode_debugadapter.BreakpointEvent("changed", { verified: true, id: bp.id, line: bp.line }));
          }, timeout++);
        }
        break;
      }
    }
    this.breakpoints.set(filename, breakpointList);
  }
  receiveClearDataBreakpoints(msg) {
    let count = msg.readInt();
    for (let i = 0; i < count; i++) {
      let id = msg.readInt();
      this.clearDataBreakpoint(id);
    }
    if (count == 0) {
      this.clearAllDataBreakpoints();
    }
  }
  setExceptionBreakPointsRequest(response, args) {
    sendBreakOptions(args.filters);
    this.sendResponse(response);
  }
  threadsRequest(response) {
    response.body = {
      threads: [
        new import_vscode_debugadapter.Thread(_ASDebugSession.THREAD_ID, "Unreal Editor")
      ]
    };
    this.sendResponse(response);
  }
  stackTraceRequest(response, args) {
    sendRequestCallStack();
    if (!this.waitingTraces)
      this.waitingTraces = new Array();
    this.waitingTraces.push(response);
  }
  receiveCallStack(msg) {
    let stack = new Array();
    let count = msg.readInt();
    let previousSourcePath;
    let previousSourceLine;
    for (let i = 0; i < count; ++i) {
      let name = msg.readString().replace(/_Implementation$/, "");
      let sourcePath = msg.readString();
      let line = msg.readInt();
      let moduleName = "";
      if (this.debugServerVersion > 0) {
        moduleName = msg.readString();
      }
      let frame = null;
      if (sourcePath && sourcePath.length != 0) {
        if (sourcePath.startsWith("::")) {
          let externalSource = new import_vscode_debugadapter.Source(
            sourcePath.substring(2),
            null,
            void 0,
            void 0,
            "bp-frame"
          );
          externalSource.presentationHint = "deemphasize";
          frame = new import_vscode_debugadapter.StackFrame(i, name + " (" + sourcePath.substring(2) + ")");
          frame.presentationHint = "label";
        } else {
          previousSourceLine = line;
          previousSourcePath = sourcePath;
          const absoluteSourcePath = this.debugServerVersion > 0 ? this.resolvePathsToSourcePath(sourcePath, moduleName) : sourcePath;
          frame = new import_vscode_debugadapter.StackFrame(i, name, this.createSource(absoluteSourcePath), line, 1);
        }
      } else {
        frame = new import_vscode_debugadapter.StackFrame(i, name);
      }
      stack.push(frame);
    }
    if (stack.length == 0) {
      stack.push(new import_vscode_debugadapter.StackFrame(0, "No CallStack"));
    }
    if (this.waitingTraces && this.waitingTraces.length > 0) {
      let response = this.waitingTraces[0];
      this.waitingTraces.splice(0, 1);
      response.body = {
        stackFrames: stack,
        totalFrames: stack.length
      };
      this.sendResponse(response);
    }
  }
  resolvePathsToSourcePath(sourcePath, moduleName) {
    if (fs.existsSync(sourcePath)) {
      return sourcePath;
    }
    let relativeFilename = moduleName.split(".").join(path.sep);
    relativeFilename += ".as";
    for (let root of this._rootPaths) {
      let absoluteFilename = root + path.sep + relativeFilename;
      if (fs.existsSync(absoluteFilename)) {
        return absoluteFilename;
      }
    }
    return sourcePath;
  }
  scopesRequest(response, args) {
    const frameReference = args.frameId;
    const scopes = new Array();
    let variablesScope = new import_vscode_debugadapter.Scope("Variables", this._variableHandles.create(frameReference + ":%local%"), false);
    variablesScope.presentationHint = "locals";
    scopes.push(variablesScope);
    scopes.push(new import_vscode_debugadapter.Scope("this", this._variableHandles.create(frameReference + ":%this%"), false));
    scopes.push(new import_vscode_debugadapter.Scope("Globals", this._variableHandles.create(frameReference + ":%module%"), false));
    response.body = {
      scopes
    };
    this.sendResponse(response);
  }
  variablesRequest(response, args) {
    const id = this._variableHandles.get(args.variablesReference);
    sendRequestVariables(id);
    if (!this.waitingVariableRequests)
      this.waitingVariableRequests = new Array();
    this.waitingVariableRequests.push({
      response,
      id
    });
  }
  combineExpression(expr, variable) {
    if (variable.startsWith("[") && variable.endsWith("]"))
      return expr + variable;
    return expr + "." + variable;
  }
  receiveVariables(msg) {
    let id = "";
    if (this.waitingVariableRequests && this.waitingVariableRequests.length > 0) {
      id = this.waitingVariableRequests[0].id;
    }
    let variables = new Array();
    let count = msg.readInt();
    for (let i = 0; i < count; ++i) {
      let debugVariable = {
        name: msg.readString(),
        value: msg.readString(),
        type: msg.readString(),
        bHasMembers: msg.readBool(),
        address: BigInt(0),
        valueSize: 0
      };
      if (this.debugServerVersion >= 2) {
        debugVariable.address = msg.readAddress();
        debugVariable.valueSize = msg.readByte();
      }
      let hint = {};
      let evalName = this.combineExpression(id, debugVariable.name);
      let varRef = 0;
      if (debugVariable.bHasMembers)
        varRef = this._variableHandles.create(evalName);
      if (debugVariable.name.endsWith("$")) {
        debugVariable.name = debugVariable.name.substr(0, debugVariable.name.length - 1);
        hint.kind = "method";
      } else if (debugVariable.name.startsWith("[") && debugVariable.name.endsWith("]")) {
        hint.kind = "data";
      }
      if (this.dataBreakpoints.has(evalName)) {
        hint.attributes = ["hasDataBreakpoint"];
      }
      let variable = {
        name: debugVariable.name,
        type: debugVariable.type,
        value: debugVariable.value,
        presentationHint: hint,
        variablesReference: varRef,
        evaluateName: debugVariable.name
      };
      debugVariable.name = evalName.replace(/^[0-9]+:%.*%./g, "");
      this.variableStore.set(this.combineExpression(id, variable.evaluateName), debugVariable);
      variables.push(variable);
    }
    if (this.waitingVariableRequests && this.waitingVariableRequests.length > 0) {
      let response = this.waitingVariableRequests[0].response;
      this.waitingVariableRequests.splice(0, 1);
      response.body = {
        variables
      };
      this.sendResponse(response);
    }
  }
  continueRequest(response, args) {
    sendContinue();
    this.sendResponse(response);
  }
  receiveContinued() {
    this.sendEvent(new import_vscode_debugadapter.ContinuedEvent(_ASDebugSession.THREAD_ID));
  }
  receiveClosed() {
    this.sendEvent(new import_vscode_debugadapter.TerminatedEvent());
  }
  pauseRequest(response, args) {
    sendPause();
    this.sendResponse(response);
  }
  receiveStopped(msg) {
    let Reason = msg.readString();
    let Description = msg.readString();
    let Text = msg.readString();
    if (Text.length != 0 && Reason == "exception") {
      this.previousException = Text;
      this.sendEvent(new import_vscode_debugadapter.StoppedEvent(Reason, _ASDebugSession.THREAD_ID, Text));
    } else {
      this.previousException = null;
      this.sendEvent(new import_vscode_debugadapter.StoppedEvent(Reason, _ASDebugSession.THREAD_ID));
    }
  }
  dataBreakpointInfoRequest(response, args, request) {
    let id = this._variableHandles.get(args.variablesReference);
    const evalName = this.combineExpression(id, args.name);
    const variable = this.variableStore.get(evalName);
    let dataId = null;
    let description = "Data Breakpoint not available";
    if (this.debugServerVersion >= 2 && variable !== void 0 && this.dataBreakpoints.get(evalName) === void 0 && variable.address !== BigInt(0)) {
      let breakpointSupported = false;
      switch (variable.valueSize) {
        case 1:
        case 2:
        case 4:
        case 8:
          breakpointSupported = true;
          break;
        default:
          breakpointSupported = false;
          break;
      }
      if (breakpointSupported) {
        dataId = evalName;
        description = `Data Breakpoint for ${variable.name} (${variable.valueSize} bytes 0x${variable.address.toString(16).toUpperCase().padStart(16, "0")})`;
      }
    }
    response.body = {
      dataId,
      description,
      accessTypes: ["write"],
      canPersist: false
    };
    this.sendResponse(response);
  }
  setDataBreakpointsRequest(response, args, request) {
    let dataBreakpointConfig = vscode.workspace.getConfiguration("UnrealAngelscript.dataBreakpoints");
    this.dataBreakpoints.clear();
    let responseBreakpoints = new Array();
    let dataBreakpoints = new Array();
    const triggerCppDataBreakpoints = dataBreakpointConfig.get("cppBreakpoints.enable");
    const hitCount = triggerCppDataBreakpoints ? dataBreakpointConfig.get("cppBreakpoints.triggerCount") : dataBreakpointConfig.get("asBreakpoints.triggerCount");
    for (let i = 0; i < args.breakpoints.length; i++) {
      let newBreakpoint = args.breakpoints[i];
      const variable = this.variableStore.get(newBreakpoint.dataId);
      const breakpointId = this.nextBreakpointId++;
      this.dataBreakpoints.set(newBreakpoint.dataId, { variable, id: breakpointId });
      let responseBreakpoint = {
        id: breakpointId,
        verified: i < _ASDebugSession.SUPPORTED_DATA_BREAKPOINT_COUNT,
        instructionReference: `0x${variable.address.toString(16).toUpperCase().padStart(16, "0")})`
      };
      responseBreakpoints.push(responseBreakpoint);
      dataBreakpoints.push({
        id: breakpointId,
        address: variable.address,
        size: variable.valueSize,
        hitCount,
        cppBreakpoint: triggerCppDataBreakpoints,
        name: `${variable.name}, ${variable.valueSize} bytes 0x${variable.address.toString(16).toUpperCase().padStart(16, "0")}`
      });
    }
    response.body = {
      breakpoints: responseBreakpoints
    };
    if (connected)
      setDataBreakpoints(dataBreakpoints);
    this.sendResponse(response);
  }
  clearDataBreakpoint(id) {
    let keyToDelete = void 0;
    for (let [key, value] of this.dataBreakpoints.entries()) {
      if (value.id === id) {
        keyToDelete = key;
        break;
      }
    }
    if (keyToDelete !== void 0) {
      this.dataBreakpoints.delete(keyToDelete);
      this.sendEvent(new import_vscode_debugadapter.BreakpointEvent("removed", { verified: false, id }));
    }
  }
  clearAllDataBreakpoints() {
    this.dataBreakpoints.forEach(function(bp, key) {
      this.sendEvent(new import_vscode_debugadapter.BreakpointEvent("removed", { verified: false, id: bp.id }));
    }, this);
    this.dataBreakpoints.clear();
  }
  exceptionInfoRequest(response, args) {
    if (!this.previousException) {
      this.sendResponse(response);
      return;
    }
    response.body = {
      exceptionId: "",
      breakMode: "unhandled",
      description: this.previousException
    };
    this.sendResponse(response);
  }
  nextRequest(response, args) {
    sendStepOver();
    this.sendResponse(response);
  }
  stepInRequest(response, args) {
    sendStepIn();
    this.sendResponse(response);
  }
  stepOutRequest(response, args) {
    sendStepOut();
    this.sendResponse(response);
  }
  restartRequest(response, args) {
    this.sendResponse(response);
  }
  evaluateRequest(response, args) {
    sendRequestEvaluate(args.expression, args.frameId);
    if (!this.waitingEvaluateRequests)
      this.waitingEvaluateRequests = new Array();
    this.waitingEvaluateRequests.push({
      expression: args.expression,
      frameId: args.frameId,
      response
    });
  }
  receiveEvaluate(msg) {
    let id = "";
    if (this.waitingEvaluateRequests && this.waitingEvaluateRequests.length > 0) {
      id = this.waitingEvaluateRequests[0].expression;
      if (!/^[0-9]+:/.test(id)) {
        id = this.waitingEvaluateRequests[0].frameId + ":" + id;
      }
    }
    let name = msg.readString();
    let value = msg.readString();
    let type = msg.readString();
    let bHasMembers = msg.readBool();
    if (this.waitingEvaluateRequests && this.waitingEvaluateRequests.length > 0) {
      let response = this.waitingEvaluateRequests[0].response;
      this.waitingEvaluateRequests.splice(0, 1);
      if (value.length == 0) {
      } else {
        response.body = {
          result: value,
          variablesReference: bHasMembers ? this._variableHandles.create(id) : 0
        };
      }
      this.sendResponse(response);
    }
  }
  createSource(filePath) {
    return new import_vscode_debugadapter.Source((0, import_path.basename)(filePath), this.convertDebuggerPathToClient(filePath), void 0, void 0, "as-script");
  }
  gatherRootPaths() {
    let roots = [];
    for (let workspaceFolder of vscode.workspace.workspaceFolders) {
      roots.push(workspaceFolder.uri.fsPath);
    }
    return roots;
  }
  GetModuleNameForFilepath(filePath) {
    for (let root of this._rootPaths) {
      let relativePath = path.relative(root, filePath);
      if (filePath.includes(relativePath)) {
        return relativePath.split(path.sep).join(".").replace(".as", "");
      }
    }
    return "";
  }
  receiveDebugVersion(msg) {
    this.debugServerVersion = msg.readInt();
  }
};
var ASDebugSession = _ASDebugSession;
ASDebugSession.THREAD_ID = 1;
ASDebugSession.SUPPORTED_DATA_BREAKPOINT_COUNT = 4;

// third-party/vscode-unreal-angelscript/extension/src/debugAdapter.ts
ASDebugSession.run(ASDebugSession);
