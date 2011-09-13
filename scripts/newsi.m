%% CPU training data %%%%%%%%%%%%%%%
load cpuLoad.dump;
nextCpu = cpuLoad(:,1);
cpuk = cpuLoad(:,2);
nn = cpuLoad(:,3);

cpuk = cpuk-mean(cpuk);

X = [cpuk nn];
cpuCoef = regress(nextCpu,X);


%% Total Cost Training data %%%%%%%

load tc.dump;
nextTc = tc(:,1);
tck = tc(:,2);
nn = tc(:,3);

tck = tck-mean(tck);

X = [tck nn];
tcCoef = regress(nextTc,X);

%% Response Time Training data %%%%

load rt.dump;
nextRt = rt(:,1);
cpuk = rt(:,2);
rtk = rt(:, 3);
nn = rt(:,4);

cpuk = cpuk-mean(cpuk);
rtk = rtk-mean(rtk);

X = [cpuk rtk nn];
rtCoef = regress(nextRt,X);

%% Calculate Coefficients Matrices %%

A = [cpuCoef(1,1) 0 0 ; 
     0 tcCoef(1,1) 0 ; 
     rtCoef(1,1) 0 rtCoef(2,1) ];

B = [cpuCoef(2,1) ; 
     tcCoef(2,1) ; 
     rtCoef(3,1)];

C = [1 0 0 ; 
     0 1 0 ; 
     0 0 1];

D = [0 ; 
     0 ; 
     0];

sys = ss(A,B,C,D,-1);

%% Transfer functions calculations %%

[num, den] = ss2tf(A, B, C, D);

%% Settling times calculations %%%%%%

eigenValue = eig(A); 

%% Controllability %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

controllability = [A*A*B A*B B];
isControllable = det(controllability);

%% calculate K controller gain %%%%%%%%%%%%%%%%%%%%

Q = diag([100 1 1]);
R = 1;
K = dlqr(A, B, Q, R);
dlmwrite('Kgains.txt', K, 'delimiter', '\n');