package travis.thenewboston.com.mhealththeme;

/**
 * Created by Shahzad Adil on 6/22/2017.
 */

public class Patient {
    int id;
    String name;
    String address;
    String phone_number;
///Constructors
    Patient()//default
    {
    }
    Patient(int _id, String _name, String _address, String _phone_number) // All parameters
    {
        id=_id;
        name=_name;
        address=_address;
        phone_number=_phone_number;
    }
    Patient( String _name, String _address, String _phone_number)// 3 Parameters
    {
        name=_name;
        address=_address;
        phone_number=_phone_number;
    }
    //All setter functions
    public void set_id(int _id)
    {
        id=_id;
    }
    public void set_name(String _name)
    {
        name=_name;
    }
    public void set_address(String _address)
    {
        address=_address;
    }
    public void set_phone_number(String _phone_number)
    {
        phone_number=_phone_number;
    }
    //All getter functions
    public int get_id()
    {
        return id;
    }
    public String get_name()
    {
        return name;
    }
    public String get_address()
    {
        return address;
    }
    public String get_phone_number()
    {
        return phone_number;
    }
}
