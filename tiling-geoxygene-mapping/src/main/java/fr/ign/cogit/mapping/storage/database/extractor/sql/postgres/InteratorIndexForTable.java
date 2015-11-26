package fr.ign.cogit.mapping.storage.database.extractor.sql.postgres;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.util.iterator.ClosableIterator;
import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.mapping.datastructure.Node;
import fr.ign.cogit.mapping.datastructure.hexaTree.TransmittedNode;
import fr.ign.cogit.mapping.datastructure.management.ScaleInfo;
import fr.ign.cogit.mapping.generalindex.GeometryConverter;
import fr.ign.cogit.mapping.generalindex.Record;
import fr.ign.cogit.mapping.storage.database.extractor.sql.PersistentStore;
import fr.ign.cogit.mapping.util.GeometryUtil;
import fr.ign.cogit.mapping.webentity.spatial.GeometryRecord;
import fr.ign.cogit.mapping.webentity.spatial.SpatialIndexException;
/*
 * Cette classe permet de fabriquer une liste d'iterateur
 * qui sera utilisé par l'index spatial...
 * cette iterateur devra renvoyer l'echelle des informations sur les noeuds
 * construits 
 * @author Dr Tsatcha D.
 */

public class InteratorIndexForTable implements
        ClosableIterator<Record<Geometry>> {
    public List<Record<Geometry>> getContent() {
        return content;
    }

    public void setContent(List<Record<Geometry>> content) {
        this.content = content;
    }

    protected static Logger LOG = LoggerFactory
            .getLogger(InteratorIndexForTable.class);

    /*
     * @param resultSet deigne les données extraites de la base de données
     */
    protected ResultSet resultSet;

    /*
     * @param table designe le nom de la table de la base de données
     */
    protected String table;

    /*
     * @param scale designe l'echelle associée aux elements de la base de
     * données
     */
    protected ScaleInfo scale;

    /*
     * @param signature designe la signature du contenu à extraire de la base de
     * données elle peut etre : geometrie ou empreinte
     */
    protected String signature;

    /*
     * @param hasNextCalled permet de fabriquer l'iterateur
     */
    private boolean hasNextCalled = false;

    /*
     * @param hasNextutilisé pour construire l'iterateur
     */
    private boolean hasNext = true;
    /*
     * @param transmittedNode permet de garder les informations necessaire à la
     * transmission par exemple : l'indiceI de la cellule hexagone qui recouvre
     * l'objet l'indiceJ de la cellule hexagone qui recouvre l'objet La
     * generation à laquelle appartient la cellule hexagone Le type de la
     * geometrie associe à cette element pour enfin de pouvoir le reconstruire à
     * la fin de la transmission... Quand les objets sortent de la base de
     * données aucune cellule hexagone leur recouvre ainsi les paramètres de la
     * cellule hexagone sont les suivantes : hexaIndiceI=0; hexaIndiceJ=0;
     * geoOrder (ordre de la generation=0;
     */
    TransmittedNode transmittedNode = null;
    /*
     * l'indice en I de la cellule hexagonale
     */
    protected int hexaIndiceI = 0;
    /*
     * L'indice en J de la cellule hexagonale
     */
    protected int hexaIndiceJ = 0;
    /*
     * L'ordre de generation de la cellule hexagonale intialement est egale à
     * zero...
     */
    protected int geoOrder = 0;
    
    /*
     *le type de la geometrique si la table n'a pas de contenu
     *nous metons rien... 
     */

    protected String geotype="inconnu";
    
    
    protected int cadreNumber=-1;
    
    
    protected int hexNumber =-1;
    protected List<Record<Geometry>> content = new ArrayList<Record<Geometry>>();
    private int pos;

    public InteratorIndexForTable(ResultSet rs, String tableParam,
            String signatureParam, ScaleInfo scaleParam) {

        this.resultSet = rs;
        this.table = tableParam;
        this.signature = signatureParam;
        this.scale = scaleParam;
        this.pos = 0;
        // la generation du noeud
        // dans ce cas c'est la prémière generation
        // dans decoupage..;
        int geoOrder = 0;
        // l'indice en I de la cellule dans sa generation
        int hexaIndiceI = 0;
        // l'indice en J de la cellule dans sa generation
        int hexaIndiceJ = 0;
        // on intiualise pour des contenus qui  n'ont pas d'elements
        transmittedNode = new TransmittedNode(hexaIndiceI, hexaIndiceJ,
                geoOrder, geotype,cadreNumber,hexNumber);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        try {
            if (hasNextCalled) {
                return hasNext;
            }

            if (hasNext) {
                hasNextCalled = true;
                hasNext = resultSet.next();
                if (!hasNext) {
                    close();
                }
            }
            return hasNext;
        } catch (SQLException e) {
            LOG.error("Error while checking whether result set has next", e);
            close();
            throw new RuntimeException(
                    "Error while checking whether result set has next", e);
            // hasNext = false;
            // return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Record<Geometry> next() {
        Record<Geometry> obj = null;

        if (!hasNext) {
            close();
            throw new RuntimeException("No more results in result set");
        }
        try {
            if (!hasNextCalled && hasNext) {
                hasNext = resultSet.next();
            }
            hasNextCalled = false;
            pos++;
            // resultSet.getObject(2).toString() designe la valeur du, srid
             geotype = GeometryUtil.getType(getGeometry());
            ;
            
            transmittedNode = new TransmittedNode(hexaIndiceI, hexaIndiceJ,
                    geoOrder, geotype,cadreNumber,hexNumber);
//            transmittedNode = new TransmittedNode(hexaIndiceI, hexaIndiceJ,
//                    geoOrder, geotype);
            obj = GeometryRecord.create(
                    generateNode(table, resultSet.getObject(2).toString(),
                            signature, transmittedNode), getGeometry());
           System.out.println(getGeometry().toText());
           System.out.println(geotype);
            // content.add(obj);
        } catch (SQLException e) {
            LOG.error("Error while moving to next row", e);
            close();
            hasNext = false;
            return null;
        }
        return obj;
    }

    public int getPosition() {
        return pos;
    }

    // modifier cette methode plus tard pour avoir les
    // contenu du noeuds de la colonne à extraire
    // cette geometry est formée à partir
    // des donnees string
    // cette methode a été modifie afin de
    // la construction de la geomatrie en fonction du srid fourni

    public Geometry getGeometry() {
        try {
            // /
            // System.out.println("la valeur de ma geometrie a été generée"+resultSet
            // .getObject(1).toString());
            return GeometryConverter.convertSQLGeometry(resultSet.getObject(1)
                    .toString(), Integer.parseInt(resultSet.getObject(2)
                    .toString()));
        } catch (SQLException e) {
            LOG.error("Error while moving to next row", e);
            close();
            return null;
        } catch (SpatialIndexException e) {
            LOG.error("Error while converting geometry", e);
            close();
            return null;
        }
    }

    // permet de gerer un noeud de stockage
    // pour l'index spatial des contenu extraite dans la base données
    // @param row les informations sur de la table
    public Node generateNode(String tableName, String srid, String signature,
            TransmittedNode trans) {
        ScaleInfo scale = null;
        // scale=generateScale(table);

        Node node = new Node(srid, this.scale, signature, tableName, pos, trans);
        return node;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Can not remove a value");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        try {
            resultSet.close();
        } catch (SQLException e) {
            LOG.error("Error while closing result set", e);
        }

    }

}
