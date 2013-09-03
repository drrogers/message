package grogers.message.utils;

import grogers.message.data.BaseCollectionBean;
import grogers.message.data.GroupBean;
import grogers.message.data.MessageBean;
import grogers.message.data.UserBean;

import java.net.UnknownHostException;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;


public class MongoUtils {

    static private MongoUtils instance;
    private Mongo mongo;
    private Morphia morphia;
    private Datastore datastore;
    
    @SuppressWarnings("rawtypes")
    private static Class[] beanClasses = {
        GroupBean.class,
        MessageBean.class,
        UserBean.class,
    };
    
    private MongoUtils() throws UnknownHostException {
        // TODO: connection properties should come from properties file
        mongo = new Mongo("localhost:27017");
        morphia = new Morphia();
        datastore = morphia.createDatastore(mongo, "message");
        
        for ( Class<BaseCollectionBean> bc : beanClasses) {
            morphia.map(bc);
        }
        
        datastore.ensureIndexes();
        datastore.ensureCaps();
    }
    
    public static MongoUtils getInstance() throws UnknownHostException {
        if (instance == null) {
            instance = newSingleton();
        }
        return instance;
    }
    
    private static synchronized MongoUtils newSingleton() throws UnknownHostException {
        if (instance == null) {
            instance = new MongoUtils();
        }
        return instance;
    }

    public Mongo getMongo() {
        return mongo;
    }

    public void setMongo(Mongo mongo) {
        this.mongo = mongo;
    }

    public Morphia getMorphia() {
        return morphia;
    }

    public void setMorphia(Morphia morphia) {
        this.morphia = morphia;
    }

    public Datastore getDatastore() {
        return datastore;
    }

    public void setDatastore(Datastore datastore) {
        this.datastore = datastore;
    }
    
}
