package fr.ign.cogit.geoxygene.osm.anonymization.db.access.queries;

import java.util.HashMap;

import org.geolatte.geom.V;

/**
 * Cette classe sert à stocker les requêtes SQL 
 * utilisée par {@link fr.ign.cogit.geoxygene.osm.anonymization.db.SQLDBPreAnonymization}.
 * Il peut s'agir de requêtes préparées on non, on considère que les 
 * classes les utilisant savent comment les utiliser. Cette classe 
 * sert donc principalement d'externalisation des caractéristiques
 * des bases de données (structure, nom des tables et colonnes) 
 * plus que de classe destinée à être utilisée de façon 
 * très modulaire avec des traitements automatiques. 
 * 
 * La classe contient une HashMap qui utilise les 
 * {@link fr.ign.cogit.geoxygene.osm.anonymization.db.access.queries.PreAnonymizationQueriesType} 
 * comme clés et leur valeurs sont les String représentant les requêtes.
 * Elle peut être accédée avec les fonctions get et put. 
 * De plus la classe peut devenir immutable en utilisant {@link setImmutable() }
 * cela indique que la HashMap ne peut plus être modifiée et tenter d'utiliser
 * la fonction put lèvera une exception.
 * 
 * @author Matthieu Dufait
 */
public class PreAnonymizationQueries {
  /**
   * Défini les requêtes pour les bases de données utilisant la
   * structure de la base du Nepal
   */
  public static final PreAnonymizationQueries NEPAL_QUERIES;
  static {
    NEPAL_QUERIES = new PreAnonymizationQueries();
    /*NEPAL_QUERIES.put(AnonymizationQueriesType.CREATE_CHANGESET_TABLE, 
        "CREATE TABLE changeset AS " +
            "(SELECT DISTINCT changeset, uid, username FROM node" +
            " UNION" +
            " SELECT DISTINCT changeset, uid, username FROM way" +
            " UNION" +
            " SELECT DISTINCT changeset, uid, username FROM relation)");*/
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.CREATE_CHANGESET_TABLE, 
        "CREATE TABLE changeset AS " +
            "(SELECT DISTINCT changeset, uid FROM node" +
            " UNION" +
            " SELECT DISTINCT changeset, uid FROM way" +
            " UNION" +
            " SELECT DISTINCT changeset, uid FROM relation)");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.CREATE_USER_TABLE, 
        "CREATE TABLE contributor AS " +
            "(SELECT DISTINCT uid FROM changeset)");
    
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.DROP_COLUMNS_NODE,
        "ALTER TABLE node DROP COLUMN uid, DROP COLUMN username");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.DROP_COLUMNS_WAY,
        "ALTER TABLE way DROP COLUMN uid, DROP COLUMN username");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.DROP_COLUMNS_MEMBER,
        "ALTER TABLE relationmember DROP COLUMN IF EXISTS username");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.DROP_COLUMNS_RELATION,
        "ALTER TABLE relation DROP COLUMN uid, DROP COLUMN  username");
    
    /*NEPAL_QUERIES.put(AnonymizationQueriesType.DROP_ID_NODE,
        "ALTER TABLE node DROP COLUMN idnode");
    NEPAL_QUERIES.put(AnonymizationQueriesType.DROP_ID_WAY,
        "ALTER TABLE way DROP COLUMN idway");
    NEPAL_QUERIES.put(AnonymizationQueriesType.DROP_ID_MEMBER,
        "ALTER TABLE relationmember DROP COLUMN idrelmb");
    NEPAL_QUERIES.put(AnonymizationQueriesType.DROP_ID_RELATION,
        "ALTER TABLE relation DROP COLUMN idrel");*/

    NEPAL_QUERIES.put(PreAnonymizationQueriesType.SELECT_ID_NODE,
        "SELECT DISTINCT id FROM node");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.SELECT_ID_WAY,
        "SELECT DISTINCT id FROM way");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.SELECT_ID_RELATION,
        "SELECT DISTINCT id FROM relation");

    NEPAL_QUERIES.put(PreAnonymizationQueriesType.ADD_VISIBLE_NODE,
        "ALTER TABLE node ADD visible boolean");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.ADD_VISIBLE_WAY,
        "ALTER TABLE way ADD visible boolean");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.ADD_VISIBLE_RELATION,
        "ALTER TABLE relation ADD visible boolean");

    NEPAL_QUERIES.put(PreAnonymizationQueriesType.UPDATE_VISIBLE_NODE,
        "UPDATE node SET visible = ? WHERE id = ? AND vnode = ?");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.UPDATE_VISIBLE_WAY,
        "UPDATE way SET visible = ? WHERE id = ? AND vway = ?");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.UPDATE_VISIBLE_RELATION,
        "UPDATE relation SET visible = ? WHERE id = ? AND vrel = ?");
    
    /*NEPAL_QUERIES.put(AnonymizationQueriesType.ADD_CHANGETYPE_NODE,
        "ALTER TABLE node ADD change_type varchar(25)");
    NEPAL_QUERIES.put(AnonymizationQueriesType.ADD_CHANGETYPE_WAY,
        "ALTER TABLE way ADD change_type varchar(25)");
    NEPAL_QUERIES.put(AnonymizationQueriesType.ADD_CHANGETYPE_RELATION,
        "ALTER TABLE relation ADD change_type varchar(25)");*/

    /*NEPAL_QUERIES.put(AnonymizationQueriesType.UPDATE_CHANGETYPE_NODE,
        "UPDATE node SET change_type = ? WHERE id = ? AND vnode = ?");
    NEPAL_QUERIES.put(AnonymizationQueriesType.UPDATE_CHANGETYPE_WAY,
        "UPDATE way SET change_type = ? WHERE id = ? AND vway = ?");
    NEPAL_QUERIES.put(AnonymizationQueriesType.UPDATE_CHANGETYPE_RELATION,
        "UPDATE relation SET change_type = ? WHERE id = ? AND vrel = ?");*/

    NEPAL_QUERIES.put(PreAnonymizationQueriesType.SELECT_CHANGESET_ID,
        "SELECT changeset FROM changeset");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.ADD_COLUMNS_CHANGESET,
        "ALTER TABLE changeset ADD created_at timestamp, " +
          "ADD time_opened interval," + 
          "ADD open boolean," +  
          "ADD comments_count integer, " +  
          "ADD tags hstore");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.UPDATE_DATA_CHANGESET,
        "UPDATE changeset SET created_at = ?::timestamp, "
        + "time_opened = ?::timestamp - ?::timestamp, "
        + "open = ?, comments_count = ?, tags = ?::hstore"
        + " WHERE changeset = ?");
    
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.DROP_DATE_NODE,
        "ALTER TABLE node DROP COLUMN datemodif");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.DROP_DATE_WAY,
        "ALTER TABLE way DROP COLUMN datemodif");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.DROP_DATE_RELATION,
        "ALTER TABLE relation DROP COLUMN datemodif");


    NEPAL_QUERIES.put(PreAnonymizationQueriesType.UPDATE_DATE_CHANGESET,
        "UPDATE changeset SET created_at = ?::timestamp WHERE changeset = ?");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.SELECT_DATE_CHANGESET,
        "SELECT changeset, created_at, extract(epoch from time_opened) "
        + "FROM changeset where created_at is not null");
    
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.ADD_VERSION_MEMBER,
        "ALTER TABLE relationmember ADD vrel integer");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.UPDATE_VERSION_MEMBER,
        "UPDATE relationmember SET idrel = ?, vrel = ? "
        + "WHERE idrel = ? AND idmb = ? AND lower(typemb) = lower(?)");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.CREATE_ANON_CONTRIBUTOR, 
        "CREATE TABLE anon_contributor as (\n" + 
        "with anon_ids as (\n" + 
        "select row_number() over (order by random()) rn,\n" + 
        "uid as anon_uid from contributor),\n" + 
        "normal_ids as (\n" + 
        "select row_number() over (order by random()) rn,\n" + 
        "uid from contributor)\n" + 
        "\n" + 
        "select normal_ids.uid, anon_ids.anon_uid\n" + 
        "from anon_ids join normal_ids\n" + 
        "on anon_ids.rn = normal_ids.rn);");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.UPDATE_CHANGESET_UID, 
        "UPDATE changeset\n" + 
        "SET uid = anon_contributor.anon_uid\n" + 
        "from anon_contributor\n" + 
        "where changeset.uid= anon_contributor.uid;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.DROP_UID_CONTRIBUTOR, 
        "alter TABLE anon_contributor DROP uid;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.REPLACE_UID_CONTRIBUTOR, 
        "alter TABLE anon_contributor RENAME COLUMN anon_uid to uid;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.DROP_TABLE_CONTRIBUTOR, 
        "drop table contributor;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.REPLACE_TABLE_CONTRIBUTOR, 
        "alter table anon_contributor RENAME TO contributor;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.CREATE_ANON_CHANGESET, 
        "create table anon_changeset as (\n" + 
        "with anon_ids as (\n" + 
        "select row_number() over (order by random()) rn,\n" + 
        "changeset as anon_changeset from changeset),\n" + 
        "normal_ids as (\n" + 
        "select row_number() over (order by random()) rn,\n" + 
        "changeset, uid, created_at, time_opened, open,\n" + 
        "comments_count, tags from changeset)\n" + 
        "\n" + 
        "select normal_ids.changeset, anon_ids.anon_changeset,\n" + 
        "uid, created_at, time_opened, open, comments_count, tags\n" + 
        "from anon_ids join normal_ids\n" + 
        "on anon_ids.rn = normal_ids.rn);");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.UPDATE_CHANGESET_IN_NODE, 
        "UPDATE node\n" + 
        "SET changeset = anon_changeset.anon_changeset\n" + 
        "from anon_changeset\n" + 
        "where node.changeset= anon_changeset.changeset;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.UPDATE_CHANGESET_IN_WAY, 
        "UPDATE way\n" + 
        "SET changeset = anon_changeset.anon_changeset\n" + 
        "from anon_changeset\n" + 
        "where way.changeset= anon_changeset.changeset;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.UPDATE_CHANGESET_IN_RELATION, 
        "UPDATE relation\n" + 
        "SET changeset = anon_changeset.anon_changeset\n" + 
        "from anon_changeset\n" + 
        "where relation.changeset= anon_changeset.changeset;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.DROP_CHANGESET_ID, 
        "alter table anon_changeset DROP changeset;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.REPLACE_CHANGESET_ID, 
        "alter table anon_changeset RENAME COLUMN anon_changeset to changeset;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.DROP_TABLE_CHANGESET, 
        "drop table changeset;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.REPLACE_TABLE_CHANGESET, 
        "alter table anon_changeset RENAME TO changeset;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.CREATE_ANON_NODE, 
        "create table anon_node as (\n" + 
        "with anon_ids as (\n" + 
        "select row_number() over (order by random()) rn,\n" + 
        "id as anon_id from node group by id),\n" + 
        "normal_ids as (\n" + 
        "select row_number() over (order by random()) rn,\n" + 
        "id from node group by id)\n" + 
        "\n" + 
        "select normal_ids.id, anon_ids.anon_id\n" + 
        "from anon_ids join normal_ids\n" + 
        "on anon_ids.rn = normal_ids.rn);");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.CREATE_ANON_WAY, 
        "create table anon_way as (\n" + 
        "with anon_ids as (\n" + 
        "select row_number() over (order by random()) rn,\n" + 
        "id as anon_id from way group by id),\n" + 
        "normal_ids as (\n" + 
        "select row_number() over (order by random()) rn,\n" + 
        "id from way group by id)\n" + 
        "\n" + 
        "select normal_ids.id, anon_ids.anon_id\n" + 
        "from anon_ids join normal_ids\n" + 
        "on anon_ids.rn = normal_ids.rn);");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.CREATE_ANON_RELATION, 
        "create table anon_relation as (\n" + 
        "with anon_ids as (\n" + 
        "select row_number() over (order by random()) rn,\n" + 
        "id as anon_id from relation group by id),\n" + 
        "normal_ids as (\n" + 
        "select row_number() over (order by random()) rn,\n" + 
        "id from relation group by id)\n" + 
        "\n" + 
        "select normal_ids.id, anon_ids.anon_id\n" + 
        "from anon_ids join normal_ids\n" + 
        "on anon_ids.rn = normal_ids.rn);");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.CREATE_WAY_NODES, 
        "create table way_nodes as (\n" + 
        "SELECT id idway, vway, a.idnode, a.idsequence\n" + 
        "FROM way\n" + 
        "LEFT JOIN LATERAL unnest(composedof)\n" + 
        "WITH ORDINALITY AS a(idnode, idsequence) ON TRUE);");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.UPDATE_WAY_NODES_IDS, 
        "UPDATE way_nodes\n" + 
        "SET idnode = anon_node.anon_id,\n" + 
        "    idway = anon_way.anon_id\n" + 
        "from anon_node, anon_way\n" + 
        "where idnode = anon_node.id\n" + 
        "and idway = anon_way.id;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.DROP_COMPOSEDOF_WAY, 
        "alter table way drop composedof;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.UPDATE_RELATIONMEMBER_REL_REF, 
        "UPDATE relationmember\n" + 
        "SET idrel = anon_relation.anon_id\n" + 
        "from anon_relation\n" + 
        "where relationmember.idrel = anon_relation.id;");  
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.UPDATE_RELATIONMEMBER_MB_RELATION, 
        "UPDATE relationmember\n" + 
        "SET idmb = anon_relation.anon_id\n" + 
        "from anon_relation\n" + 
        "where relationmember.idmb = anon_relation.id AND typemb = 'Relation';");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.UPDATE_RELATIONMEMBER_MB_WAY, 
        "UPDATE relationmember\n" + 
        "SET idmb = anon_way.anon_id\n" + 
        "from anon_way\n" + 
        "where relationmember.idmb = anon_way.id AND typemb = 'Way';");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.UPDATE_RELATIONMEMBER_MB_NODE, 
        "UPDATE relationmember\n" + 
        "SET idmb = anon_node.anon_id\n" + 
        "from anon_node\n" + 
        "where relationmember.idmb = anon_node.id AND typemb = 'Node';");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.UPDATE_ID_NODE, 
        "UPDATE node\n" + 
        "SET id = anon_node.anon_id\n" + 
        "FROM anon_node\n" + 
        "WHERE node.id = anon_node.id;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.UPDATE_ID_WAY, 
        "UPDATE way\n" + 
        "SET id = anon_way.anon_id\n" + 
        "FROM anon_way\n" + 
        "WHERE way.id = anon_way.id;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.UPDATE_ID_RELATION, 
        "UPDATE relation\n" + 
        "SET id = anon_relation.anon_id\n" + 
        "FROM anon_relation\n" + 
        "WHERE relation.id = anon_relation.id;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.DROP_ANON_NODE, 
        "drop table anon_node;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.DROP_ANON_WAY, 
        "drop table anon_way;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.DROP_ANON_RELATION, 
        "drop table anon_relation;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.DROP_BAD_ID_NODE, 
        "alter table node drop column idnode;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.DROP_BAD_ID_WAY, 
        "alter table way drop column idway;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.DROP_BAD_ID_RELATION, 
        "alter table relation drop column idrel;");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.DROP_BAD_ID_RELATIONMEMBER, 
        "alter table relationmember drop column idrelmb;");
    
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.ADD_PRIMARY_CONTRIBUTOR, 
        "ALTER TABLE contributor ADD PRIMARY KEY (uid);");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.ADD_PRIMARY_WAY_NODES, 
        "ALTER TABLE way_nodes ADD PRIMARY KEY (idway, vway, idnode, idsequence);");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.ADD_PRIMARY_CHANGESET, 
        "ALTER TABLE changeset ADD PRIMARY KEY (changeset);");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.ADD_PRIMARY_NODE, 
        "ALTER TABLE node ADD PRIMARY KEY (id, vnode);");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.ADD_PRIMARY_WAY, 
        "ALTER TABLE way ADD PRIMARY KEY (id, vway);");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.ADD_PRIMARY_RELATION, 
        "ALTER TABLE relation ADD PRIMARY KEY (id, vrel);");
    NEPAL_QUERIES.put(PreAnonymizationQueriesType.ADD_PRIMARY_RELATIONMEMBER, 
        "ALTER TABLE relationmember ADD PRIMARY KEY (idrel, idmb, vrel);");

    
    if(NEPAL_QUERIES.querylist.size() != PreAnonymizationQueriesType.values().length)
      throw new IllegalStateException("Problème dans le remplissage des requêtes");
    
    NEPAL_QUERIES.setImmutable();
  }
  /**
   * HashMap des requêtes
   */
  private HashMap<PreAnonymizationQueriesType, String> querylist;
  /**
   * Indique si querylist est modifiable
   */
  private boolean mutable;
  
  /**
   * Initialise une HashMap vide et mutable à vrai
   */
  public PreAnonymizationQueries() {
    this.querylist = new HashMap<PreAnonymizationQueriesType, String>();
    mutable = true;
  }

  /**
   * Initialise une nouvelle HashMap remplie avec les éléments de querylist
   * et mutable à vrai
   * @param querylist
   */
  public PreAnonymizationQueries(HashMap<PreAnonymizationQueriesType, String> querylist) {
    this(querylist, true);
  }

  /**
   * Initialise une nouvelle HashMap remplie avec les éléments de querylist
   * et mutable à la valeur du paramètre mutable
   * @param querylist
   * @param mutable
   */
  public PreAnonymizationQueries(HashMap<PreAnonymizationQueriesType, String> querylist,
      boolean mutable) {
    this();
    this.querylist.putAll(querylist);
    this.mutable = mutable;
  }
  
  /**
   * Même comportement que put de HashMap pour querylist
   * sauf si mutable est à false, renvoi une exception 
   * UnsupportedOperationException dans ce cas
   * @see java.util.HashMap#put(K, V)
   * @param key
   * @param value
   * @return
   */
  public String put(PreAnonymizationQueriesType key, String value) {
    if(!mutable)
      throw new UnsupportedOperationException("Can not modify this instance any longer.");
    return querylist.put(key, value);
  }

  /**
   * Retourne la valeur liée à la clé 
   * donnée en paramètre
   * @param key
   * @return
   */
  public String get(PreAnonymizationQueriesType key) {
    return querylist.get(key);
  }

  /**
   * Retourne la valeur de mutable
   * @return
   */
  public boolean isMutable() {
    return mutable;
  }

  /**
   * Fixe mutable à false
   */
  public void setImmutable() {
    this.mutable = false;
  }
}
