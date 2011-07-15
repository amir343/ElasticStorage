%% CPU training data %%%%%%%%%%%%%%%
load cpuLoad.dump;
nextCpu = cpuLoad(:,1);
tpk = cpuLoad(:,2);
cpuk = cpuLoad(:,3);
nn = cpuLoad(:,4);

tpk = tpk-mean(tpk);
cpuk = cpuk-mean(cpuk);

X = [tpk cpuk nn];
cpuCoef = regress(nextCpu,X);

%% Bandwidth Training data %%%%%%%%

load cpuSTD.dump;
nextCpuSTD = cpuSTD(:,1);
tpk = cpuSTD(:,2);
cpuStdk = cpuSTD(:,3);
nn = cpuSTD(:,4);

tpk = tpk-mean(tpk);
cpuStdk = cpuStdk-mean(cpuStdk);

X = [tpk cpuStdk nn];
cpuSTDCoef = regress(nextCpuSTD,X);

%% Total Cost Training data %%%%%%%

load tc.dump;
nextTc = tc(:,1);
tpk = tc(:,2);
bwk = tc(:,3);
tck = tc(:,4);
nn = tc(:,5);

tpk = tpk-mean(tpk);
bwk = bwk-mean(bwk);
tck = tck-mean(tck);

X = [tpk bwk tck nn];
tcCoef = regress(nextTc,X);

%% Response Time Training data %%%%

load rt.dump;
nextRt = rt(:,1);
tpk = rt(:,2);
cpuk = rt(:,3);
bwk = rt(:,4);
nn = rt(:,5);

tpk = tpk-mean(tpk);
cpuk = cpuk-mean(cpuk);
bwk = bwk-mean(bwk);

X = [tpk cpuk bwk nn];
rtCoef = regress(nextRt,X);

%% Calculate Coefficients Matrices %%

A = [cpuCoef(1,1) cpuCoef(2,1) 0 0 ; 
     cpuSTDCoef(1,1) 0 cpuSTDCoef(2,1) 0 ; 
     tcCoef(1,1) 0 tcCoef(2,1) tcCoef(3,1) ; 
     rtCoef(1,1) rtCoef(2,1) rtCoef(3,1) 0];

B = [cpuCoef(3,1) ; 
     cpuSTDCoef(3,1) ; 
     tcCoef(4,1) ; 
     rtCoef(4,1)];

C = [1 0 0 0 ; 
     0 1 0 0 ; 
     0 0 1 0 ; 
     0 0 0 1];

D = [0 ; 
     0 ; 
     0 ; 
     0];

sys = ss(A,B,C,D,-1);

%% Transfer functions calculations %%

[num, den] = ss2tf(A, B, C, D);

%% Settling times calculations %%%%%%

eigenValue = eig(A); 

%% Controllability %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

controllability = [A*A*A*B A*A*B A*B B];
isControllable = det(controllability);

%% calculate K controller gain %%%%%%%%%%%%%%%%%%%%

Q = diag([10 10 1 1]);
R = 1;
K = dlqr(A, B, Q, R);
