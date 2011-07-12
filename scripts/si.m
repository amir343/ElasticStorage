%%  Number of nodes state %%%%%%%%%%%%%%%
load input_nn_state.dump;
input_nn = input_nn_state(:,1);

%%  Throughput state %%%%%%%%%%%%%%%
load input_tp_state.dump;
input_tp = input_tp_state(:,1);

%%  CPU state %%%%%%%%%%%%%%%
load output_cpu_state.dump;
output_cpu = output_cpu_state(:,1);

%%  Bandwidth state %%%%%%%%%%%%%%%
load output_bw_state.dump;
output_bw = output_bw_state(:,1);

%%  Total Cost state %%%%%%%%%%%%%%%
load output_tc_state.dump;
output_tc = output_tc_state(:,1);

%%  Response Time state %%%%%%%%%%%%%%%
load output_rt_state.dump;
output_rt = output_rt_state(:,1);


%% CPU training data %%%%%%%%%%%%%%%
load cpu.dump;
nextCpu = cpu(:,1);
tpk = cpu(:,2);
cpuk = cpu(:,3);
nn = cpu(:,4);

X = [tpk cpuk nn];
cpuCoef = regress(nextCpu,X);

%% Bandwidth Training data %%%%%%%%

load bw.dump;
nextBw = bw(:,1);
tpk = bw(:,2);
bwk = bw(:,3);
nn = bw(:,4);

X = [tpk bwk nn];
bwCoef = regress(nextBw,X);

%% Total Cost Training data %%%%%%%

load tc.dump;
nextTc = tc(:,1);
tpk = tc(:,2);
bwk = tc(:,3);
tck = tc(:,4);
nn = tc(:,5);

X = [tpk bwk tck nn];
tcCoef = regress(nextTc,X);

%% Response Time Training data %%%%

load rt.dump;
nextRt = rt(:,1);
tpk = rt(:,2);
cpuk = rt(:,3);
bwk = rt(:,4);
nn = rt(:,5);

X = [tpk cpuk bwk nn];
rtCoef = regress(nextRt,X);

%% Calculate Coefficients Matrices %%

A = [cpuCoef(1,1) cpuCoef(2,1) 0 0 ; 
     bwCoef(1,1) 0 bwCoef(2,1) 0 ; 
     tcCoef(1,1) 0 tcCoef(2,1) tcCoef(3,1) ; 
     rtCoef(1,1) rtCoef(2,1) rtCoef(3,1) 0];

B = [cpuCoef(3,1) ; 
     bwCoef(3,1) ; 
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

Q = diag([1 1 1 1]);
R = 1;
K = dlqr(A, B, Q, R);
