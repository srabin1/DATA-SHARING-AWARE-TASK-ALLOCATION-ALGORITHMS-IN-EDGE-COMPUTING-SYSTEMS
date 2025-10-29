# DATA-SHARING-AWARE-TASK-ALLOCATION-ALGORITHMS-IN-EDGE-COMPUTING-SYSTEMS

Edge computing allows end-user devices to offload heavy computation to nearby edge
servers for reduced latency, maximized profit, and/or minimized energy consumption. Data
dependent tasks that analyze locally acquired sensing data are one of the most common
candidates for task offloading in edge computing. Thus, the total latency and network load
are affected by the total amount of data transferred from end-user devices to the selected
edge servers. Most existing solutions for task allocation in edge computing do not consider
that some user tasks may operate on the same data items. Making the task allocation
algorithm aware of the existing data sharing characteristics of tasks can help reduce the
network load at a negligible profit loss by allocating more tasks sharing data on the same
server.
In this dissertation, we formulate the data sharing-aware task allocation problem that
makes decisions on task allocation for maximized profit and minimized network load by
considering the data-sharing characteristics of tasks. In addition, because the problem is
NP-hard, we design an offline algorithm called DSTA, which finds a close to optimal solution
to the problem in polynomial time. We analyze the performance of our algorithm
against a state-of-the-art baseline that only maximizes profit. Our analysis shows that
DSTA leads to about eight times lower data load on the network while being within 1.03
times of the total profit on average compared to the baseline. In addition, we introduce
the Online Data Sharing-aware Task Allocation (ODSTA) problem and design online algorithms
for task allocation in edge computing that take into account the sharing of data
among the tasks offloaded to the same server. We perform an extensive performance analysis
by comparing our proposed data sharing-aware online algorithms with several baseline
online sharing-oblivious algorithms. The results show that our algorithms are able to reduce
the amount of data transferred in the network by 30.2% to 92.8% and the number of
utilized servers by 1% to 82.8% compared to the sharing-oblivious baseline algorithms.
We also augment these online algorithms with a local search phase that iteratively attempts
to improve the solutions obtained by our data sharing-aware algorithms by exploring
the neighborhood of the current solution and making minor modifications. Our extensive
experimental performance analysis shows that the algorithms augmented with local search
reduce the number of utilized servers by 9.1% to 66.7% compared to the data sharing-aware
online algorithms at the expense of a small increase in the amount of data transferred in the
network and a small increase in execution time. We provide a summary of the findings that
can be used as a guideline for choosing a specific algorithm for a given practical scenario
characterized by the tasks’ CPU demand and their data sharing characteristics.