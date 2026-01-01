class AExample
{
    // Boolean literals in Meta specifiers
    UPROPERTY(EditInstanceOnly, Meta = (MakeEditWidget=true))
    FVector WidgetLocation;

    // Multiple Meta values with boolean literals
    UPROPERTY(EditDefaultsOnly, Meta = (ClampMin=0, ClampMax=100, AllowPreserveRatio=false))
    int Health = 100;

    // nullptr literal in Meta
    UPROPERTY(BlueprintReadWrite, Meta = (ExposeOnSpawn=true, DefaultValue=nullptr))
    UObject MyObject;

    // String values in Meta
    UPROPERTY(EditAnywhere, Meta = (Category="Gameplay", ToolTip="Player health value"))
    float PlayerHealth;

    // Numeric values in Meta
    UPROPERTY(EditAnywhere, Meta = (UIMin=0.0, UIMax=1.0, ClampMin=0, ClampMax=100))
    float NormalizedValue;

    // Mixed value types
    UPROPERTY(EditAnywhere, BlueprintReadWrite, Meta = (AllowPrivateAccess=true, ExposeOnSpawn=false, Priority=10))
    int32 MixedMetaProperty;
}