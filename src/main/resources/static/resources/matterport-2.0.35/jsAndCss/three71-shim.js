
if (typeof THREE !== 'undefined') {

  THREE.Box3.prototype.setFromObjects = function(objects) {
    this.makeEmpty();
    objects.forEach(function(child) {
      if (!child.geometry.boundingBox) {
        child.geometry.computeBoundingBox();
      }
      this.union(child.geometry.boundingBox);
    }.bind(this));
    return this;
  }

  THREE.Mesh.prototype.raycast = ( function () {

  	var inverseMatrix = new THREE.Matrix4();
  	var ray = new THREE.Ray();
  	var sphere = new THREE.Sphere();

  	var vA = new THREE.Vector3();
  	var vB = new THREE.Vector3();
  	var vC = new THREE.Vector3();

  	var tempA = new THREE.Vector3();
  	var tempB = new THREE.Vector3();
  	var tempC = new THREE.Vector3();

  	return function raycast( raycaster, intersects ) {

  		var geometry = this.geometry;
  		var material = this.material;

  		if ( material === undefined ) return;

  		// Checking boundingSphere distance to ray

  		if ( geometry.boundingSphere === null ) geometry.computeBoundingSphere();

  		sphere.copy( geometry.boundingSphere );
  		sphere.applyMatrix4( this.matrixWorld );

  		if ( raycaster.ray.isIntersectionSphere( sphere ) === false ) {

  			return;

  		}

  		// Check boundingBox before continuing

  		inverseMatrix.getInverse( this.matrixWorld );
  		ray.copy( raycaster.ray ).applyMatrix4( inverseMatrix );

  		if ( geometry.boundingBox !== null ) {

  			if ( ray.isIntersectionBox( geometry.boundingBox ) === false ) {

  				return;

  			}

  		}

  		var a, b, c;

  		if ( geometry instanceof THREE.BufferGeometry ) {

  			var attributes = geometry.attributes;

  			if ( attributes.index !== undefined ) {

  				var indices = attributes.index.array;
  				var positions = attributes.position.array;
  				var offsets = geometry.drawcalls;

  				if ( offsets.length === 0 ) {

  					offsets = [ { start: 0, count: indices.length, index: 0 } ];

  				}

  				for ( var oi = 0, ol = offsets.length; oi < ol; ++ oi ) {

  					var start = offsets[ oi ].start;
  					var count = offsets[ oi ].count;
  					var index = offsets[ oi ].index;

  					for ( var i = start, il = start + count; i < il; i += 3 ) {

  						a = index + indices[ i ];
  						b = index + indices[ i + 1 ];
  						c = index + indices[ i + 2 ];

  						vA.fromArray( positions, a * 3 );
  						vB.fromArray( positions, b * 3 );
  						vC.fromArray( positions, c * 3 );

  						if ( material.side === THREE.BackSide ) {

  							var intersectionPoint = ray.intersectTriangle( vC, vB, vA, true );

  						} else {

  							var intersectionPoint = ray.intersectTriangle( vA, vB, vC, material.side !== THREE.DoubleSide );

  						}

  						if ( intersectionPoint === null ) continue;

  						intersectionPoint.applyMatrix4( this.matrixWorld );

  						var distance = raycaster.ray.origin.distanceTo( intersectionPoint );

  						if ( distance < raycaster.near || distance > raycaster.far ) continue;

  						intersects.push( {

  							distance: distance,
  							point: intersectionPoint,
  							face: new THREE.Face3( a, b, c, THREE.Triangle.normal( vA, vB, vC ) ),
  							faceIndex: Math.floor( i / 3 ), // triangle number in indices buffer semantics
  							object: this

  						} );

  					}

  				}

  			} else {

  				var positions = attributes.position.array;

  				for ( var i = 0, il = positions.length; i < il; i += 9 ) {

  					vA.fromArray( positions, i );
  					vB.fromArray( positions, i + 3 );
  					vC.fromArray( positions, i + 6 );

  					if ( material.side === THREE.BackSide ) {

  						var intersectionPoint = ray.intersectTriangle( vC, vB, vA, true );

  					} else {

  						var intersectionPoint = ray.intersectTriangle( vA, vB, vC, material.side !== THREE.DoubleSide );

  					}

  					if ( intersectionPoint === null ) continue;

  					intersectionPoint.applyMatrix4( this.matrixWorld );

  					var distance = raycaster.ray.origin.distanceTo( intersectionPoint );

  					if ( distance < raycaster.near || distance > raycaster.far ) continue;

  					a = i / 3;
  					b = a + 1;
  					c = a + 2;

  					intersects.push( {

  						distance: distance,
  						point: intersectionPoint,
  						face: new THREE.Face3( a, b, c, THREE.Triangle.normal( vA, vB, vC ) ),
  						index: a, // triangle number in positions buffer semantics
  						object: this

  					} );

  				}

  			}

  		} else if ( geometry instanceof THREE.Geometry ) {

  			var isFaceMaterial = material instanceof THREE.MeshFaceMaterial;
  			var materials = isFaceMaterial === true ? material.materials : null;

  			var vertices = geometry.vertices;
  			var faces = geometry.faces;

  			for ( var f = 0, fl = faces.length; f < fl; f ++ ) {

  				var face = faces[ f ];
  				var faceMaterial = isFaceMaterial === true ? materials[ face.materialIndex ] : material;

  				if ( faceMaterial === undefined ) continue;

  				a = vertices[ face.a ];
  				b = vertices[ face.b ];
  				c = vertices[ face.c ];

  				if ( faceMaterial.morphTargets === true ) {

  					var morphTargets = geometry.morphTargets;
  					var morphInfluences = this.morphTargetInfluences;

  					vA.set( 0, 0, 0 );
  					vB.set( 0, 0, 0 );
  					vC.set( 0, 0, 0 );

  					for ( var t = 0, tl = morphTargets.length; t < tl; t ++ ) {

  						var influence = morphInfluences[ t ];

  						if ( influence === 0 ) continue;

  						var targets = morphTargets[ t ].vertices;

  						vA.addScaledVector( tempA.subVectors( targets[ face.a ], a ), influence );
  						vB.addScaledVector( tempB.subVectors( targets[ face.b ], b ), influence );
  						vC.addScaledVector( tempC.subVectors( targets[ face.c ], c ), influence );

  					}

  					vA.add( a );
  					vB.add( b );
  					vC.add( c );

  					a = vA;
  					b = vB;
  					c = vC;

  				}

  				if ( faceMaterial.side === THREE.BackSide ) {

  					var intersectionPoint = ray.intersectTriangle( c, b, a, true );

  				} else {

  					var intersectionPoint = ray.intersectTriangle( a, b, c, faceMaterial.side !== THREE.DoubleSide );

  				}

  				if ( intersectionPoint === null ) continue;

  				intersectionPoint.applyMatrix4( this.matrixWorld );

  				var distance = raycaster.ray.origin.distanceTo( intersectionPoint );

  				if ( distance < raycaster.near || distance > raycaster.far ) continue;

  				intersects.push( {

  					distance: distance,
  					point: intersectionPoint,
  					face: face,
  					faceIndex: f,
  					object: this

  				} );

  			}

  		}

  	};

  }() );

}
