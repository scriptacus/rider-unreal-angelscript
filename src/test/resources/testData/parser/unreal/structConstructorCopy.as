struct FTestStruct
{
    FTestStruct(int value)
    {
        MyValue = value;
    }

    UPROPERTY(EditAnywhere)
    int MyValue;
}
