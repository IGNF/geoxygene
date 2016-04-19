
Architecture - GeOxygene Plateform
###################################

This page describes how GeOxygene Plateform is structured.

Component Architecture
************************ 

.. container:: centerside
     
    .. figure:: /documentation/resources/img/architecture/ArchitectureGeoxygene_02.png
       :width: 750px
       
       Figure 1 : GeOxygene Architecture




The figure below shows how all sub-modules fit together :

.. container:: centerside

	.. raw:: html
	
		<dependencies></dependencies>    
		
		<script type="text/javascript"> 
		
		var width = 400,
	    height = 400,
	    outerRadius = Math.min(width, height) / 2 - 10,
	    innerRadius = outerRadius - 24;
	
		var formatPercent = d3.format(".1%");
		
		var arc = d3.svg.arc()
		    .innerRadius(innerRadius)
		    .outerRadius(outerRadius);
		
		var layout = d3.layout.chord()
		    .padding(.04)
		    .sortSubgroups(d3.descending)
		    .sortChords(d3.ascending);
		
		var path = d3.svg.chord()
		    .radius(innerRadius);
		
		var svg = d3.select("dependencies").append("svg")
		    .attr("width", width)
		    .attr("height", height)
		  .append("g")
		    .attr("id", "circle")
		    .attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");
		
		svg.append("circle")
		    .attr("r", outerRadius);
		
		d3.csv("modules.csv", function(cities) {
		  d3.json("matrix.json", function(matrix) {
		
		    // Compute the chord layout.
		    layout.matrix(matrix);
		
		    // Add a group per neighborhood.
		    var group = svg.selectAll(".group")
		        .data(layout.groups)
		      .enter().append("g")
		        .attr("class", "group")
		        .on("mouseover", mouseover);
		
		    // Add a mouseover title.
		    group.append("title").text(function(d, i) {
			  return cities[i].completename;
		    });
		
		    // Add the group arc.
		    var groupPath = group.append("path")
		        .attr("id", function(d, i) { return "group" + i; })
		        .attr("d", arc)
		        .style("fill", function(d, i) { return cities[i].color; });
		
		    // Add a text label.
		    var groupText = group.append("text")
		        .attr("x", 6)
		        .attr("dy", 15);
		
		    groupText.append("textPath")
		        .attr("xlink:href", function(d, i) { return "#group" + i; })
		        .text(function(d, i) { return cities[i].name; })
		        .style("font-size", "11px");
		
		    // Remove the labels that don't fit. :(
		     groupText.filter(function(d, i) { return groupPath[0][i].getTotalLength() / 2 - 8 < this.getComputedTextLength(); })
		         .remove();
		
		    // Add the chords.
		    var chord = svg.selectAll(".chord")
		        .data(layout.chords)
		        .enter().append("path")
		        .attr("class", "chord")
		        .style("fill", function(d) { return cities[d.source.index].color; })
		        .attr("d", path);
		
		    // Add an elaborate mouseover title for each chord.
		    chord.append("title").text(function(d) {
		      return cities[d.source.index].completename
		          + " → " + cities[d.target.index].completename
		          + ": " + formatPercent(d.source.value)
		          + "\n" + cities[d.target.index].completename
		          + " → " + cities[d.source.index].completename
		          + ": " + formatPercent(d.target.value);
		    });
		
		    function mouseover(d, i) {
		      chord.classed("fade", function(p) {
		        return p.source.index != i
		            && p.target.index != i;
		      });
		    }
		  });
		});
	
		</script>


  
Modules presentation
**********************

All modules are presented here, functionalities and concepts. 
                    
                    
Noyau 
=======

   .. raw:: html
   
      <div class="divTable">
	      <p class="tableau">
	          <span class="side1">geoxygene-api</span>
	          <span class="side2">
	               Définition des interfaces permettant de travailler avec de l’information spatiale.
	          </span>
	      </p>
	      <p class="tableau">
	          <span class="side1">geoxygene-spatial</span>
	          <span class="side2">
	               Implémentation des principales classes géométriques et topologiques. 
	               Algorithmes spatiaux : géométriques, généralisations, index, …
				   <br/>
		           <a href='geometry.html'>Geometrie dans GeOxygene</a>
	          </span>
	      </p>
	   	  <p class="tableau">
	          <span class="side1">geoxygene-feature</span>
	          <span class="side2">
	               Implémentation des objets géographiques.
		           <br/>
		           <a href='feature.html'>Feature : structure et manipulation</a>
	          </span>
	      </p>
	      <p class="tableau">
	          <span class="side1">geoxygene-io,<br/>geoxygene-database</span>
	          <span class="side2">
	               Chargement et export des données géographiques.
	          </span>
	      </p>
	      <p class="tableau">
	          <span class="side1">geoxygene-style</span>
	          <span class="side2">
	               Le package style permet de décrire des styles cartographiques dans le formalisme des normes OGC 
		           SLD (Styled Layer Descriptor) et SE (Symbology Encoding).
		           <br/>
		           <a href='motif.html'>Style : motifs & poncifs</a>
		           <br/>
		           <a href='contour.html'>Style : gestion des contours</a>
	          </span>
	      </p>
	      <p class="tableau">
	          <span class="side1">geoxygene-filter</span>
	          <span class="side2">
	               Le package filter permet de filtrer pour produire un nouveau jeu de résultats
		           <br/>
		           <a href='filter.html'>Expression de filtres</a>
	          </span>
	      </p>
		  <p class="tableau">
	          <span class="side1">geoxygene-schemageo</span>
	          <span class="side2">
	               Schéma géographique des objets
	          </span>
	      </p>
	  </div>
	
	
Contributions
===============

   .. raw:: html
   
      <div class="divTable">
	      <p class="tableau">
	          <span class="side1">geoxygene-semio</span>
	          <span class="side2">
	               Module dédié aux travaux autour de la légende d'une carte
	               <br/>
	               <a href='../application/semiology.html'>Semiology Tools</a>
	          </span>
	      </p>
	      <p class="tableau">
	          <span class="side1">geoxygene-contrib</span>
	          <span class="side2">
	               Module dédié aux graphes, la conflation, la qualité des données
	               <br/>
	               <a href='../application/topological-map.html'>La Carte Topologique</a>
	               <br/>
	               <a href='../application/data-matching.html'>Appariement de données</a>
	          </span>
	      </p>
	      <p class="tableau">
	          <span class="side1">geoxygene-matching</span>
	          <span class="side2">
	               Module dédié à l'appariement
	          </span>
	      </p>
	      <p class="tableau">
	          <span class="side1">geoxygene-cartagen</span>
	          <span class="side2">
	               Module dédié à la généralisation de données géographiques
	          </span>
	      </p>
	      <p class="tableau">
	          <span class="side1">geoxygene-osm</span>
	          <span class="side2">
	               Module dédié aux manipulations des données OSM
	               <br/>
	               <a href='../application/osm.html'>Données OSM</a>
	          </span>
	      </p>
	      <p class="tableau">
	          <span class="side1">geoxygene-sig3d</span>
	          <span class="side2">
	               Module dédié à la visualisation et à la manipulation de données 3D
	               <br/>
	               <a href='../application/3d.html'>3D</a>
	          </span>
	      </p>
	  </div>
	  
Viewer
=======

   .. raw:: html
   
   	  <div class="divTable">
	      <p class="tableau">
	          <span class="side1">geoxygene-appli</span>
	          <span class="side2">
	          	Application graphique 2D
	          	<br/>
	          	<a href='plugin.html'>Plugin dans l'interface graphique 2D</a>
	          </span>
	      </p>
      </div>

  
Extensions
============

   .. raw:: html
   
   	  <div class="divTable">
	      <p class="tableau">
	          <span class="side1">geoxygene-ojplugin</span>
	          <span class="side2">
	          	Plugins GeOxygene pour OpenJump : appariement de réseaux, indicateur de qualité.
	          </span>
	      </p>
	      <p class="tableau">
	          <span class="side1">geoxygene-wps</span>
	          <span class="side2">
	          	Web services pour GeoServer : appariement de réseaux, réseaux topologiques
	          </span>
	      </p>
      </div>

Applications
===============

   .. raw:: html
   
   	  <div class="divTable">
	      <p class="tableau">
	          <span class="side1">geoxygene-geopensim</span>
	          <span class="side2">
	          	<a href='http://geopensim.ign.fr'>http://geopensim.ign.fr</a>
	          </span>
	      </p>
	      <p class="tableau">
	          <span class="side1">geoxygene-pearep</span>
	          <span class="side2">
	          	<a href='http://www.tandfonline.com/doi/abs/10.1080/15230406.2013.809233#.VDqeYRZXvuc'>ScaleMaster 2.0: a ScaleMaster extension to monitor automatic multi-scales generalizations</a>
	          </span>
	      </p>
      </div>
  
Library
=========

   .. raw:: html
   
   	  <div class="divTable">
	      <p class="tableau">
	          <span class="side1">geoxygene-util</span>
	          <span class="side2"></span>
	      </p>
	      <p class="tableau">
	          <span class="side1">geoxygene-spatial-relation</span>
	          <span class="side2"></span>
	      </p>
      </div>
  