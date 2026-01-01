void Test()
{
    switch (value)
    {
        case 1:
            DoOne();
            fallthrough;
        case 2:
            DoTwo();
            break;
    }
}
