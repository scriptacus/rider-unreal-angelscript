struct FTestStruct
{
    FTestStruct(int Value)
    {
        MyValue = Value;
    }

    UPROPERTY(EditAnywhere)
    int MyValue;
}
