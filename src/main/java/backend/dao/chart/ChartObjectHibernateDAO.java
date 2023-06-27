package backend.dao.chart;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import backend.dao.ObjectUnchangedException;
import backend.model.chart.HorizontalLine;

/**
 * Provides access to chart object database persistence using Hibernate.
 * 
 * @author Michael
 */
public class ChartObjectHibernateDAO implements ChartObjectDAO {
	/**
	 * Factory for database session.
	 */
	protected EntityManagerFactory sessionFactory;
	
	
	/**
	 * Default constructor.
	 * 
	 * @param sessionFactory The database session factory.
	 */
	public ChartObjectHibernateDAO(final EntityManagerFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	
	@Override
	public void insertHorizontalLine(HorizontalLine horizontalLine) throws Exception {
		// TODO Auto-generated method stub

	}

	
	@Override
	public void deleteHorizontalLine(HorizontalLine horizontalLine) throws Exception {
		// TODO Auto-generated method stub

	}

	
	@Override
	public List<HorizontalLine> getHorizontalLines(Integer instrumentId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public HorizontalLine getHorizontalLine(Integer id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void updateHorizontalLine(HorizontalLine horizontalLine) throws ObjectUnchangedException, Exception {
		// TODO Auto-generated method stub

	}
}
