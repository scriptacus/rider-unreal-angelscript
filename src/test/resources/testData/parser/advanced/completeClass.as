UCLASS()
class AMyActor : AActor
{
    UPROPERTY(EditAnywhere)
    int Health = 100;

    UPROPERTY(BlueprintReadOnly)
    FString Name;

    default Name = "DefaultName";

    UFUNCTION(BlueprintCallable)
    void TakeDamage(int Amount)
    {
        Health -= Amount;
        if (Health <= 0)
        {
            Destroy();
        }
    }

    void BeginPlay()
    {
        Print(f"Actor {Name} spawned with {Health} health");
    }
}
