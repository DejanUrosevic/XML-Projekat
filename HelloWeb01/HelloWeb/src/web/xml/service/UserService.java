package web.xml.service;

import java.io.File;

import web.xml.model.User;
import web.xml.model.Users;

public interface UserService extends CrudService<User>
{
	public Users unmarshall(File f);
	
	public void marshall(File f);
}
