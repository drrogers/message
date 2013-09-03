package grogers.message.data.dao;

import java.util.List;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.query.Query;

public abstract class BaseDAO<T,K> extends BasicDAO<T, K> {
    public static final int MIN_LIMIT = 1;
	public static final int MAX_LIMIT = 1000;

    public BaseDAO(Class<T> entityClass, Datastore ds) {
        super(entityClass, ds);
    }

	
    /**
     * Returns a "page" of results.  
     * @param offset - ordinal position, post sorting, of the first returned object
     * @param limit - maximum number of results; MIN_LIMIT <= limit < MAX_LIMIT
     * @param orderBy - optional property name to sort by
     * @return
     */
    public List<T> getPage(Query<T> query, int offset, int limit, String orderBy) {
    	if (orderBy != null)
    		query.order(orderBy);
    	
    	if (limit > MAX_LIMIT)
    		limit = MAX_LIMIT;
    	if (limit < MIN_LIMIT)
    		limit = MIN_LIMIT;
    	if (offset < 0)
    		offset = 0;
    	
    	List<T> results = query.offset(offset).limit(limit).asList();
    	return results;
    }

}
