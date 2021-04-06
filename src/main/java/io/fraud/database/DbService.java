package io.fraud.database;

import com.qaguild.dbeaver.DBViewer;
import com.qaguild.dbeaver.runners.jdbi.JDBIQueryRunner;
import io.fraud.database.dao.DealDao;
import io.fraud.database.model.Deal;
import io.fraud.kafka.ProjectConfig;
import org.aeonbits.owner.ConfigFactory;
import org.awaitility.Awaitility;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.postgresql.ds.PGSimpleDataSource;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DbService {

//    private final Jdbi jdbi;
    private final DBViewer dbViewer;
    private DealDao dealDao;

    public DbService() {
        ProjectConfig config = ConfigFactory.create(ProjectConfig.class);

        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName(config.dbHost());
        ds.setPortNumber(config.dbPort());
        ds.setDatabaseName(config.dbName());
        ds.setUser(config.dbUser());
        ds.setPassword(config.dbPassword());

//        this.jdbi = Jdbi.create(ds);
//        this.jdbi.installPlugin(new SqlObjectPlugin());
        this.dbViewer = new DBViewer(JDBIQueryRunner.create(ds));
        dealDao = dbViewer.create(DealDao.class);
    }

    /*
    public Deal findDealById(int id) { return jdbi.onDemand(DealDao.class).findById(id); }

    public List<Deal> findByCurrency(String currency) { return jdbi.onDemand(DealDao.class).findByCurrency(currency); }
    */

    public Deal findDealById(int id) { return dealDao.findOne(id); }

    public List<Deal> findByCurrency(String currency) { return dealDao.findByCurrency(currency); }

    public List<Deal> findBySource(String source) {
        Awaitility.waitAtMost(10, TimeUnit.SECONDS)
                .until(() -> dealDao.findBySource(source).size() != 0);

        return dealDao.findBySource(source);
    }

    public void deleteById(int id) { dealDao.delete(id); }
}
