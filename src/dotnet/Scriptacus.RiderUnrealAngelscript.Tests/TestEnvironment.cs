using System.Threading;
using JetBrains.Application.BuildScript.Application.Zones;
using JetBrains.ReSharper.Feature.Services;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.TestFramework;
using JetBrains.TestFramework;
using JetBrains.TestFramework.Application.Zones;
using NUnit.Framework;

[assembly: Apartment(ApartmentState.STA)]

namespace Scriptacus.RiderUnrealAngelscript.Tests
{
    [ZoneDefinition]
    public class AngelscriptRiderPluginTestEnvironmentZone : ITestsEnvZone, IRequire<PsiFeatureTestZone>, IRequire<IAngelscriptRiderPluginZone> { }

    [ZoneMarker]
    public class ZoneMarker : IRequire<ICodeEditingZone>, IRequire<ILanguageCSharpZone>, IRequire<AngelscriptRiderPluginTestEnvironmentZone> { }

    [SetUpFixture]
    public class AngelscriptRiderPluginTestsAssembly : ExtensionTestEnvironmentAssembly<AngelscriptRiderPluginTestEnvironmentZone> { }
}
