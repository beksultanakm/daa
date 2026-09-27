"""
plot.py

Reads the CSV files produced by Benchmark.java (in results/tables/) and
generates the required plots (Assignment 2, Section 8) into
results/plots/.

Usage (after running the Java benchmark once, from the results/ folder):
    pip install pandas matplotlib
    python plot.py
"""

import pandas as pd
import matplotlib.pyplot as plt
import os

TABLES = "tables"
PLOTS = "plots"
os.makedirs(PLOTS, exist_ok=True)


def savefig(name):
    path = os.path.join(PLOTS, name)
    plt.tight_layout()
    plt.savefig(path, dpi=150)
    plt.close()
    print("Saved", path)


# ---------------------------------------------------------------------
# Workload 1: Random Access
# ---------------------------------------------------------------------
def plot_workload1():
    df = pd.read_csv(os.path.join(TABLES, "workload1_random_access.csv"))
    plt.figure()
    for structure, group in df.groupby("structure"):
        plt.plot(group["n"], group["avg_time_ns"] / 1e6, marker="o", label=structure)
    plt.xscale("log")
    plt.yscale("log")
    plt.xlabel("n (initial elements)")
    plt.ylabel("Avg. execution time (ms) for 10,000 get(index) calls")
    plt.title("Workload 1: Random Access — Time vs n")
    plt.legend()
    plt.grid(True, which="both", ls="--", alpha=0.4)
    savefig("workload1_time_vs_n.png")


# ---------------------------------------------------------------------
# Workload 2: Search
# ---------------------------------------------------------------------
def plot_workload2():
    df = pd.read_csv(os.path.join(TABLES, "workload2_search.csv"))
    plt.figure()
    for structure, group in df.groupby("structure"):
        plt.plot(group["n"], group["avg_time_ns"] / 1e6, marker="o", label=structure)
    plt.xscale("log")
    plt.yscale("log")
    plt.xlabel("n (initial elements)")
    plt.ylabel("Avg. execution time (ms) for 1,000 contains(x) calls")
    plt.title("Workload 2: Search — Time vs n")
    plt.legend()
    plt.grid(True, which="both", ls="--", alpha=0.4)
    savefig("workload2_time_vs_n.png")

    plt.figure()
    for structure, group in df.groupby("structure"):
        plt.plot(group["n"], group["avg_comparisons"], marker="o", label=structure)
    plt.xscale("log")
    plt.yscale("log")
    plt.xlabel("n (initial elements)")
    plt.ylabel("Avg. comparisons per contains(x) batch (1,000 calls)")
    plt.title("Workload 2: Search — Comparisons vs n")
    plt.legend()
    plt.grid(True, which="both", ls="--", alpha=0.4)
    savefig("workload2_comparisons_vs_n.png")


# ---------------------------------------------------------------------
# Workload 3: Insertion / Removal
# ---------------------------------------------------------------------
def plot_workload3():
    df = pd.read_csv(os.path.join(TABLES, "workload3_insert_remove.csv"))
    for operation in ["insert", "remove"]:
        plt.figure()
        sub = df[df["operation"] == operation]
        for (structure, position), group in sub.groupby(["structure", "position"]):
            plt.plot(group["n"], group["avg_time_ns"] / 1e6, marker="o",
                     label=f"{structure} ({position})")
        plt.xscale("log")
        plt.yscale("log")
        plt.xlabel("n (initial elements)")
        plt.ylabel(f"Avg. execution time (ms) for 1,000 {operation}s")
        plt.title(f"Workload 3: {operation.capitalize()} — Time vs n")
        plt.legend()
        plt.grid(True, which="both", ls="--", alpha=0.4)
        savefig(f"workload3_{operation}_time_vs_n.png")

    plt.figure()
    for (structure, position, operation), group in df.groupby(["structure", "position", "operation"]):
        plt.plot(group["n"], group["avg_element_moves"], marker="o",
                 label=f"{structure} ({position}, {operation})")
    plt.xscale("log")
    plt.yscale("log")
    plt.xlabel("n (initial elements)")
    plt.ylabel("Avg. element moves/hops per operation batch")
    plt.title("Workload 3: Element Movements vs n")
    plt.legend(fontsize=7)
    plt.grid(True, which="both", ls="--", alpha=0.4)
    savefig("workload3_moves_vs_n.png")


# ---------------------------------------------------------------------
# Workload 4: Priority Processing (MinHeap)
# ---------------------------------------------------------------------
def plot_workload4():
    df = pd.read_csv(os.path.join(TABLES, "workload4_heap.csv"))
    plt.figure()
    plt.plot(df["n"], df["avg_insert_time_ns"] / 1e6, marker="o", label="insert (n calls)")
    plt.plot(df["n"], df["avg_extract_time_ns"] / 1e6, marker="o", label="extractMin (n calls)")
    plt.xscale("log")
    plt.yscale("log")
    plt.xlabel("n (elements)")
    plt.ylabel("Avg. execution time (ms)")
    plt.title("Workload 4: MinHeap — Time vs n")
    plt.legend()
    plt.grid(True, which="both", ls="--", alpha=0.4)
    savefig("workload4_time_vs_n.png")

    plt.figure()
    plt.plot(df["n"], df["avg_comparisons"], marker="o", color="darkred")
    plt.xscale("log")
    plt.yscale("log")
    plt.xlabel("n (elements)")
    plt.ylabel("Avg. comparisons (insert + extract phases)")
    plt.title("Workload 4: MinHeap — Comparisons vs n")
    plt.grid(True, which="both", ls="--", alpha=0.4)
    savefig("workload4_comparisons_vs_n.png")


if __name__ == "__main__":
    plot_workload1()
    plot_workload2()
    plot_workload3()
    plot_workload4()
    print("All plots generated.")
