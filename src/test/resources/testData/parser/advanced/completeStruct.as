USTRUCT(BlueprintType)
struct FPlayerStats
{
    UPROPERTY()
    int Level = 1;

    UPROPERTY()
    float Experience = 0.0f;

    UPROPERTY()
    TArray<FString> Achievements;

    void AddExperience(float Amount)
    {
        Experience += Amount;
        if (Experience >= 100.0f)
        {
            Level++;
            Experience = 0.0f;
        }
    }
}
