# GeoSemagrowBL

GeoSemagrowBL is a demonstrator for federated geospatial query processing. The demonstrator performs a federated GeoSPARQL query over three endpoints. The first endpoint is a regular SPARQL endpoint that contains RDF data and the remaining two endpoints are GeoSPARQL endpoints that contain geospatial RDF data. For our implementation, we make use of [Semagrow](http://semagrow.github.io/), a state-of-the-art federated SPARQL query processing engine.

### Prerequisites

* [Kubernetes](https://kubernetes.io/) cluster, version >=1.8.0
* [KoBE](https://github.com/semagrow/kobe/tree/feat-operator/operator) Benchmarking Engine

### Setup

We assume that we have access to a Kubernetes cluster, such that the Kobe Operator from the KoBE Benchmarking Engine has already been deployed. In order to setup the demonstrator, login to your cluster and take the following steps:

```sh
git clone https://github.com/semagrow/geosemagrowbl.git
cd geosemagrowbl/deploy
```

First, deploy the datasets:

```sh
kubectl apply -f misc/pt-postgis.yaml
kubectl apply -f misc/sq-postgis.yaml
```
```sh
kubectl apply -f datasets/colors.yaml
kubectl apply -f datasets/pt-strabon.yaml
kubectl apply -f datasets/sq-strabon.yaml
```

Wait for the datasets to load. In order to verify that the loading is complete, the logs of the colors pod contains the line `Server online at 1111` while for the strabon pods the line `<CENTER><P>Data stored successfully!</P></CENTER>`

Execute the following commands to help the query optimization process:

```sh
kubectl exec -it kobenfs bash
head -n 1 exports/pt-strabon/dump/points.nt > exports/pt-strabon/dump/dump.nt
rm exports/pt-strabon/dump/points.nt
head -n 1 exports/sq-strabon/dump/squares.nt > exports/sq-strabon/dump/dump.nt
rm exports/sq-strabon/dump/squares.nt 
exit
```

Initialize the helper Semagrow federations:

```sh
kubectl apply -f federators/semagrow.yaml
kubectl apply -f benchmarks/pt-colors.yaml
kubectl apply -f benchmarks/sq-colors.yaml
```
```sh
kubectl apply -f experiments/kobeexp-ptcl.yaml
kubectl apply -f experiments/kobeexp-sqcl.yaml
```

Create the working directory for the Query Processor component:

```sh
kubectl exec -it kobenfs bash
mkdir /exports/gsgbl/
chmod 777 /exports/gsgbl/
exit
```

Initialize the Query Processor and Proxy components:

```sh
kubectl apply -f misc/tmp-postgis.yaml
kubectl apply -f datasets/tmp-strabon.yaml
kubectl apply -f misc/proxy.yaml
```

Expose the port of the Proxy so that queries can be issued:

```sh
kubectl expose svc proxy --name proxy-public --type NodePort
```

To discover the exposed port, execute the following command:

```sh
kubectl get svc proxy-public
```

The response should look like this:
```
NAME           TYPE       CLUSTER-IP        EXTERNAL-IP   PORT(S)              AGE
proxy-public   NodePort   xxx.xxx.xxx.xxx   <none>        80:<NODE_PORT>/TCP   xxx
```

Finally, you can issue your query:
```sh
cd ../queries/query2
./runQuery.sh http://<NODE_IP>:<NODE_PORT>
```
where `<NODE_IP>` is the IP of any node of your cluster and `<NODE_PORT>` the exposed port obtained previously.
