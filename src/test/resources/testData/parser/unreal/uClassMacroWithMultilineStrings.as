UCLASS(HideDropdown, Meta = (DisplayName = "Race:
Camera"), HideCategories="Rendering
Actor")
class UTestClass : UObject
{
    UPROPERTY(EditAnywhere, Meta = (ToolTip = "This is a
multi-line
tooltip"))
    int32 TestProperty;

    UFUNCTION(BlueprintCallable, Meta = (Category = "My
Category"))
    void TestFunction() {}
}
