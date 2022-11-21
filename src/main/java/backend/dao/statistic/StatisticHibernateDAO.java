package backend.dao.statistic;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import backend.dao.ObjectUnchangedException;
import backend.model.instrument.InstrumentType;
import backend.model.statistic.Statistic;

/**
 * Provides access to Statistic database persistence using Hibernate.
 * 
 * @author Michael
 */
public class StatisticHibernateDAO implements StatisticDAO {
	/**
	 * Factory for database session.
	 */
	protected EntityManagerFactory sessionFactory;
	
	
	/**
	 * Default constructor.
	 * 
	 * @param sessionFactory The database session factory.
	 */
	public StatisticHibernateDAO(final EntityManagerFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	
	@Override
	public void insertStatistic(Statistic statistic) throws Exception {
		// TODO Auto-generated method stub

	}

	
	@Override
	public void deleteStatistic(Statistic statistic) throws Exception {
		// TODO Auto-generated method stub

	}

	
	@Override
	public List<Statistic> getStatistics(InstrumentType instrumentType) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public Statistic getStatistic(Integer id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void updateStatistic(Statistic statistic) throws ObjectUnchangedException, Exception {
		// TODO Auto-generated method stub

	}
}
