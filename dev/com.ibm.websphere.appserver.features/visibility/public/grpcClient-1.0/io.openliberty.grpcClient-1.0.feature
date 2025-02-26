-include= ~${workspace}/cnf/resources/bnd/feature.props
symbolicName=io.openliberty.grpcClient-1.0
visibility=public
singleton=true
IBM-App-ForceRestart: install, \
  uninstall
IBM-API-Package: \
  io.grpc.netty; type="third-party", \
  io.netty.handler.ssl; type="third-party", \
  io.openliberty.grpc.internal.client; type="internal"
IBM-ShortName: grpcClient-1.0
Subsystem-Version: 1.0.0
Subsystem-Name: gRPC Client 1.0
-features= \
  com.ibm.websphere.appserver.servlet-4.0; ibm.tolerates:="5.0", \
  io.openliberty.grpcClient1.0.internal.ee-8.0; ibm.tolerates:="9.0", \
  com.ibm.websphere.appserver.internal.slf4j-1.7.7, \
  io.openliberty.internal.grpc-1.0
-bundles=\
  io.openliberty.org.apache.commons.logging
kind=ga
edition=core
WLP-Activation-Type: parallel
