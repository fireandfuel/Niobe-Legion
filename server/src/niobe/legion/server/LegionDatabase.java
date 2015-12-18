package niobe.legion.server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import niobe.legion.shared.Utils;
import niobe.legion.shared.data.LegionRight;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;
import niobe.legion.shared.model.GroupEntity;
import niobe.legion.shared.model.UserEntity;

public class LegionDatabase extends AbstractDatabase
{
    private static LegionDatabase database;

    public static LegionDatabase init(String type, String persistenceName, String... args) throws SQLException
    {
        if(LegionDatabase.database == null)
        {
            LegionDatabase.database = new LegionDatabase(type, persistenceName, args);
        }
        return LegionDatabase.database;
    }

    LegionDatabase(String type, String persistenceName, String... args) throws SQLException
    {
        super(type, persistenceName, args);

        // Check if database have users
        List<String> users = this.getUsers();
        if(users == null || users.isEmpty())
        {
            this.createInitialUsers();
        }
    }

    public final List<String> getUsers()
    {
        List<String> users = null;
        List<UserEntity> results = this.getResults(UserEntity.class);

        if(results != null)
        {
            users = results.stream().map(UserEntity::getName).collect(Collectors.toList());
        }

        return users;
    }

    public final UserEntity getUser(String name)
    {
        return this.getResult("user.getByName", UserEntity.class, entry("name", this.encrypt(name)));
    }


    final void createInitialUsers()
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] entropy = new byte[1024];
            Utils.random.nextBytes(entropy);
            md.update(entropy, 0, 1024);
            String password = new BigInteger(1, md.digest()).toString(16).substring(0, 12);

            GroupEntity group = new GroupEntity();
            group.setName(this.encrypt("users"));
            group.setActive(true);
            group.setRights(groupRightsFor(LegionRight.USER_RIGHT, LegionRight.LOGIN));
            this.insert(group);

            group = new GroupEntity();
            group.setName(this.encrypt("inactive"));
            this.insert(group);

            GroupEntity admins = new GroupEntity();
            admins.setName(this.encrypt("administrators"));
            admins.setActive(true);
            admins.setRights(groupRightsFor(LegionRight.USER_RIGHT,
                                            LegionRight.LOGIN,
                                            LegionRight.ADMINISTRATION,
                                            LegionRight.SERVER_ADMINISTRATION,
                                            LegionRight.STOP_SERVER,
                                            LegionRight.RESTART_SERVER,
                                            LegionRight.USER_ADMINISTRATION,
                                            LegionRight.ADD_USER,
                                            LegionRight.RENAME_USER,
                                            LegionRight.DELETE_USER,
                                            LegionRight.SET_USER_PASSWORD,
                                            LegionRight.SET_USER_GROUP,
                                            LegionRight.GROUP_ADMINISTRATION,
                                            LegionRight.ADD_GROUP,
                                            LegionRight.RENAME_GROUP,
                                            LegionRight.DELETE_GROUP,
                                            LegionRight.RIGHT_ADMINISTRATION,
                                            LegionRight.SET_RIGHT,
                                            LegionRight.UNSET_RIGHT));

            UserEntity user = new UserEntity();
            user.setName("root");
            user.setPassword(password);
            user.setGroup(admins);
            this.insert(user);

            System.out.println("Root password is: " + password);
            System.out.println("SECURITY WARNING: Please change the root password later");
        } catch(NoSuchAlgorithmException e)
        {
            Logger.exception(LegionLogger.DATABASE, e);
        }
    }
}
