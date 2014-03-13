package haui.tool.shell.engine;

import haui.tool.shell.engine.JShellCommandProcessor.CommandLineContainer;

public interface ArgumentIteration
{

  public abstract CommandLineContainer getCommandLineContainer();

  public abstract boolean hasNextArgument();

  public abstract String getArguments();

  public abstract String[] getArgumentArray();

  public abstract String getArgumentsFromPosition();

  public abstract String[] getArgumentArrayFromPosition();

  public abstract String readNextArgument();

  public abstract String readArgumentAt( int iArgPos);

}