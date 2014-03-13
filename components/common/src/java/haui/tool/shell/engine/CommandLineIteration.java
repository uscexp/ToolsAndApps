package haui.tool.shell.engine;

import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;

public interface CommandLineIteration
{
  public abstract CommandLineContainer getCommandLineContainer();

  public abstract boolean hasNextCommandLine();

  public abstract String readNextCommandLine();

}