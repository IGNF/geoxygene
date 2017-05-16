package fr.ign.cogit.geoxygene.osm.schema;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;

public class OSMDefaultFeature extends AbstractFeature implements OSMFeature {

	/**
	 * The name of the last contributor for {@code this} feature.
	 */
	private String contributor;
	private OsmSource source = OsmSource.UNKNOWN;
	private OsmCaptureTool captureTool = OsmCaptureTool.UNKNOWN;
	private long osmId;
	private int changeSet;
	/**
	 * The version of the feature, i.e. 1 if it's just been created, 2 if
	 * someone modified it once, etc.
	 */
	private int version;
	/**
	 * The user (i.e. contributor) id for this version of the feature.
	 */
	private int uid;
	private Map<String, String> tags;
	/**
	 * The date the contribution was added in OSM.
	 */
	private Date date;

	public OSMDefaultFeature(String contributor, IGeometry geom, int id, int changeSet, int version, int uid, Date date,
			Map<String, String> tags) {
		super();
		this.contributor = contributor;
		this.setGeom(geom);
		this.osmId = id;
		this.uid = uid;
		this.changeSet = changeSet;
		this.version = version;
		this.date = date;
		this.tags = tags;
		this.id = new Long(osmId).intValue();
	}

	@Override
	public IFeature cloneGeom() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContributor() {
		return this.contributor;
	}

	@Override
	public void setContributor(String contributor) {
		this.contributor = contributor;
	}

	@Override
	public OsmSource getSource() {
		return this.source;
	}

	@Override
	public void setSource(OsmSource source) {
		this.source = source;
	}

	@Override
	public OsmCaptureTool getCaptureTool() {
		return this.captureTool;
	}

	@Override
	public void setCaptureTool(OsmCaptureTool captureTool) {
		this.captureTool = captureTool;
	}

	@Override
	public long getOsmId() {
		return this.osmId;
	}

	@Override
	public void setOsmId(long id) {
		this.osmId = id;
	}

	@Override
	public int getChangeSet() {
		return this.changeSet;
	}

	@Override
	public void setChangeSet(int changeSet) {
		this.changeSet = changeSet;
	}

	@Override
	public int getVersion() {
		return this.version;
	}

	@Override
	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int getUid() {
		return this.uid;
	}

	@Override
	public void setUid(int uid) {
		this.uid = uid;
	}

	@Override
	public Map<String, String> getTags() {
		return this.tags;
	}

	@Override
	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	@Override
	public Date getDate() {
		return this.date;
	}

	@Override
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public Object getAttribute(String nomAttribut) {
		Object value = getTags().get(nomAttribut);
		if (value != null)
			return value;

		AttributeType attribute = new AttributeType();
		attribute.setNomField(nomAttribut);
		attribute.setMemberName(nomAttribut);
		if (attribute.getMemberName() != null && attribute.getMemberName().equals("geom")) { //$NON-NLS-1$
			return this.getGeom();
		}
		if (attribute.getMemberName() != null && attribute.getMemberName().equals("topo")) { //$NON-NLS-1$
			return this.getTopo();
		}
		if (attribute.getMemberName() != null && attribute.getMemberName().equals("id")) { //$NON-NLS-1$
			return this.getId();
		}
		Object valeur = null;
		String nomFieldMaj = null;
		if (((AttributeType) attribute).getNomField().length() == 0) {
			nomFieldMaj = ((AttributeType) attribute).getNomField();
		} else {
			nomFieldMaj = Character.toUpperCase(((AttributeType) attribute).getNomField().charAt(0))
					+ ((AttributeType) attribute).getNomField().substring(1);
		}
		String nomGetFieldMethod = "get" + nomFieldMaj; //$NON-NLS-1$
		Class<?> classe = this.getClass();
		while (!classe.equals(Object.class)) {
			try {
				Method methodGetter = classe.getDeclaredMethod(nomGetFieldMethod, (Class[]) null);
				valeur = methodGetter.invoke(this, (Object[]) null);
				return valeur;
			} catch (SecurityException e) {
				if (AbstractFeature.logger.isDebugEnabled()) {
					AbstractFeature.logger.debug("SecurityException pendant l'appel de la méthode " + nomGetFieldMethod
							+ " sur la classe " + classe);
				}
			} catch (IllegalArgumentException e) {
				if (AbstractFeature.logger.isDebugEnabled()) {
					AbstractFeature.logger.debug("IllegalArgumentException pendant l'appel de la méthode "
							+ nomGetFieldMethod + " sur la classe " + classe);
				}
			} catch (NoSuchMethodException e) {
				// if (logger.isDebugEnabled())
				// logger.debug("La m�thode "+nomGetFieldMethod+" n'existe pas
				// dans la classe "+classe);
			} catch (IllegalAccessException e) {
				if (AbstractFeature.logger.isDebugEnabled()) {
					AbstractFeature.logger.debug("IllegalAccessException pendant l'appel de la méthode "
							+ nomGetFieldMethod + " sur la classe " + classe);
				}
			} catch (InvocationTargetException e) {
				if (AbstractFeature.logger.isDebugEnabled()) {
					AbstractFeature.logger.debug("InvocationTargetException pendant l'appel de la méthode "
							+ nomGetFieldMethod + " sur la classe " + classe);
				}
			}
			classe = classe.getSuperclass();
		}
		// réessayer si le getter est du genre isAttribute, ie pour un booléen
		nomGetFieldMethod = "is" + nomFieldMaj; //$NON-NLS-1$
		classe = this.getClass();
		while (!classe.equals(Object.class)) {
			try {
				Method methodGetter = classe.getDeclaredMethod(nomGetFieldMethod, (Class[]) null);
				valeur = methodGetter.invoke(this, (Object[]) null);
				return valeur;
			} catch (SecurityException e) {
				if (AbstractFeature.logger.isDebugEnabled()) {
					AbstractFeature.logger.debug("SecurityException pendant l'appel de la méthode " + nomGetFieldMethod
							+ " sur la classe " + classe);
				}
			} catch (IllegalArgumentException e) {
				if (AbstractFeature.logger.isDebugEnabled()) {
					AbstractFeature.logger.debug("IllegalArgumentException pendant l'appel de la méthode "
							+ nomGetFieldMethod + " sur la classe " + classe);
				}
			} catch (NoSuchMethodException e) {
				// if (logger.isDebugEnabled())
				// logger.debug("La méthode "+nomGetFieldMethod+" n'existe pas
				// dans la classe "+classe);
			} catch (IllegalAccessException e) {
				if (AbstractFeature.logger.isDebugEnabled()) {
					AbstractFeature.logger.debug("IllegalAccessException pendant l'appel de la méthode "
							+ nomGetFieldMethod + " sur la classe " + classe);
				}
			} catch (InvocationTargetException e) {
				if (AbstractFeature.logger.isDebugEnabled()) {
					AbstractFeature.logger.debug("InvocationTargetException pendant l'appel de la méthode "
							+ nomGetFieldMethod + " sur la classe " + classe);
				}
			}
			classe = classe.getSuperclass();
		}

		return null;
	}
}
